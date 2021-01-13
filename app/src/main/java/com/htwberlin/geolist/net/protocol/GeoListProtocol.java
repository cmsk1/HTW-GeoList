package com.htwberlin.geolist.net.protocol;

import com.htwberlin.geolist.data.interfaces.DataStorage;
import com.htwberlin.geolist.data.interfaces.TaskListRepository;
import com.htwberlin.geolist.data.interfaces.UserRepository;
import com.htwberlin.geolist.data.models.TaskList;
import com.htwberlin.geolist.logic.GeoListLogic;
import com.htwberlin.geolist.logic.TaskListMerger;
import com.htwberlin.geolist.net.packet.PacketInputStream;
import com.htwberlin.geolist.net.packet.PacketOutputStream;
import com.htwberlin.geolist.net.packet.PacketType;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

public class GeoListProtocol extends AbstractProtocol implements IProtocol {
    private final TaskListRepository taskRepo;
    private final UserRepository userRepo;
    private final boolean isMaster;
    private final UUID shareTasklist;
    private TaskList activeTasklist;
    private TaskList[] tasklists;
    private int tasklistIndex;

    protected GeoListProtocol(DataStorage storage, boolean isMaster, UUID shareTasklist) {
        this.taskRepo = storage.getTaskRepo();
        this.userRepo = storage.getUserRepo();
        this.isMaster = isMaster;
        this.shareTasklist = shareTasklist;
    }

    @Override
    protected IProtocolState start() {
        if (this.isMaster) {
            if (this.shareTasklist == null) {
                return this::masterBeginSync;
            } else {
                return this::masterBeginShare;
            }
        } else {
            return this::slaveBegin;
        }
    }

    private IProtocolState masterBeginSync() throws IOException {
        this.send(PacketType.SNC);
        PacketInputStream fromSlave = this.receive();
        int slaveStatus = fromSlave.getPacketType();
        String partnerSign = this.getPeerDevice().getSignature();

        switch (slaveStatus) {
            case PacketType.SGN:
                this.makeTasklistSnapshot();
                return this::masterRevExchange;
            case PacketType.DNY:
                this.userRepo.deleteUser(partnerSign);
                return null;
            default:
                throw new IllegalProtocolState(slaveStatus);
        }
    }

    private void makeTasklistSnapshot() {
        this.tasklistIndex = 0;
        String partnerSign = this.getPeerDevice().getSignature();
        Collection<TaskList> tasklists = this.taskRepo.getAllListsSharedWith(partnerSign);
        this.tasklists = tasklists.toArray(new TaskList[0]);
    }

    private IProtocolState masterBeginShare() throws IOException {
        PacketOutputStream toSlave = new PacketOutputStream(PacketType.SHR);
        TaskList tasklist = this.taskRepo.getList(this.shareTasklist);
        toSlave.writeUTF(tasklist.getUuid().toString());
        this.send(toSlave);

        PacketInputStream fromSlave = this.receive();
        int slaveStatus = fromSlave.getPacketType();
        String partnerSign = this.getPeerDevice().getSignature();
        String displayName = this.getPeerDevice().getDisplayName();

        switch (slaveStatus) {
            case PacketType.SGN:
                this.userRepo.addUser(partnerSign, displayName);
                this.taskRepo.addUserToSharedList(this.shareTasklist, partnerSign);
                this.makeTasklistSnapshot();
                return this::masterRevExchange;
            case PacketType.DNY:
                return null;
            default:
                throw new IllegalProtocolState(slaveStatus);
        }
    }

    private IProtocolState masterRevExchange() throws IOException {
        if (this.tasklistIndex >= this.tasklists.length) {
            this.send(PacketType.END);
            return null;
        }
        this.activeTasklist = this.tasklists[this.tasklistIndex++];
        PacketOutputStream toSlave = new PacketOutputStream(PacketType.VER);
        toSlave.writeUTF(this.activeTasklist.getUuid().toString());
        this.send(toSlave);

        PacketInputStream fromSlave = this.receive(PacketType.REV);
        long tasklistHash = fromSlave.readLong();

        if (tasklistHash != this.activeTasklist.getContentHash()) {
            this.send(PacketType.GET);
            return this::masterSync;
        } else {
            this.send(PacketType.FIN);
            return this::masterRevExchange;
        }
    }

    private IProtocolState masterSync() throws IOException {
        PacketInputStream fromSlave = this.receive(PacketType.CMP);
        TaskList otherList = fromSlave.readSerializable();
        TaskListMerger merger = new TaskListMerger(this.activeTasklist, otherList);
        TaskList merged = merger.merge();

        PacketOutputStream toSlave = new PacketOutputStream(PacketType.MDF);
        toSlave.writeSerializable(merged);
        this.send(toSlave);
        this.overrideTasklist(merged, this.activeTasklist);
        return this::masterRevExchange;
    }

    private IProtocolState slaveBegin() throws IOException {
        PacketInputStream fromMaster = this.receive();
        int masterStatus = fromMaster.getPacketType();
        String partnerSign = this.getPeerDevice().getSignature();
        String displayName = this.getPeerDevice().getDisplayName();

        switch (masterStatus) {
            case PacketType.SNC:
                if (!this.userRepo.isKnownUser(partnerSign)) {
                    this.send(PacketType.DNY);
                    return null;
                } else {
                    this.send(PacketType.SGN);
                    return this::slaveAwaitRev;
                }
            case PacketType.SHR:
                if (GeoListLogic.instance().requestSharePermission()) {
                    UUID tasklistId = UUID.fromString(fromMaster.readUTF());
                    this.userRepo.addUser(partnerSign, displayName);
                    this.addSharedList(tasklistId);
                    this.taskRepo.addUserToSharedList(tasklistId, partnerSign);
                    this.send(PacketType.SGN);
                    return this::slaveAwaitRev;
                } else {
                    this.send(PacketType.DNY);
                    return null;
                }
            default:
                throw new IllegalProtocolState(masterStatus);
        }
    }

    private IProtocolState slaveAwaitRev() throws IOException {
        PacketInputStream fromMaster = this.receive();
        int masterStatus = fromMaster.getPacketType();

        switch (masterStatus) {
            case PacketType.VER:
                UUID tasklistId = UUID.fromString(fromMaster.readUTF());
                this.activeTasklist = this.taskRepo.getList(tasklistId);

                PacketOutputStream toMaster = new PacketOutputStream(PacketType.REV);
                toMaster.writeLong(this.activeTasklist.getContentHash());
                this.send(toMaster);
                return this::slaveAwaitSync;
            case PacketType.END:
                return null;
            default:
                throw new IllegalProtocolState(masterStatus);
        }
    }

    private IProtocolState slaveAwaitSync() throws IOException {
        PacketInputStream fromMaster = this.receive();
        int masterStatus = fromMaster.getPacketType();

        switch (masterStatus) {
            case PacketType.GET:
                return this::slaveGet;
            case PacketType.FIN:
                return this::slaveAwaitRev;
            default:
                throw new IllegalProtocolState(masterStatus);
        }
    }

    private IProtocolState slaveGet() throws IOException {
        PacketOutputStream toMaster = new PacketOutputStream(PacketType.CMP);
        toMaster.writeSerializable(this.activeTasklist);
        this.send(toMaster);

        PacketInputStream fromMaster = this.receive(PacketType.MDF);
        TaskList merged = fromMaster.readSerializable();
        this.overrideTasklist(merged, this.activeTasklist);
        return this::slaveAwaitRev;
    }

    private void overrideTasklist(TaskList merged, TaskList original) {
        merged.setOwned(original.isOwned());
        merged.setSharedUsers(original.getSharedUsers());
        this.taskRepo.saveList(merged);
    }

    private void addSharedList(UUID tasklistId) {
        TaskList tasklist = new TaskList(tasklistId);
        tasklist.setDisplayName("");
        tasklist.setOwned(false);
        this.taskRepo.saveList(tasklist);
    }
}
