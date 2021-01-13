package com.htwberlin.geolist.gui;

import android.content.Context;
import android.view.InputDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.htwberlin.geolist.R;
import com.htwberlin.geolist.logic.GeoListLogic;
import com.htwberlin.geolist.net.IPeersListener;
import com.htwberlin.geolist.net.helper.NetInterface;
import com.htwberlin.geolist.net.p2p.Device;
import com.htwberlin.geolist.net.p2p.INetwork;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class DeviceViewAdapter extends RecyclerView.Adapter<DeviceViewAdapter.DeviceViewHolder> implements IPeersListener {
    private final Runnable onDeviceSelected;
    private final Context context;
    private Collection<Device> devices;
    private UUID listId;

    public DeviceViewAdapter(Context context, UUID listId, Runnable onDeviceSelected) {
        this.context = context;
        this.devices = GeoListLogic.getNetInterface().getNetwork().getDevices();
        this.listId = listId;
        this.onDeviceSelected = onDeviceSelected;
    }

    @NotNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.device_entry, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        if (position < this.devices.size()) {
            Device device = this.getDevice(position);
            holder.text.setText(device.getDisplayName());

            holder.body.setOnClickListener(v -> {
                NetInterface net = GeoListLogic.getNetInterface();
                net.scheduleShare(device, this.listId);
                this.onDeviceSelected.run();
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.devices.size();
    }

    private Device getDevice(int position) {
        Iterator<Device> iterator = this.devices.iterator();
        Device device = null;
        int i = 0;

        while (iterator.hasNext() && i <= position) {
            device = iterator.next();
            i++;
        }
        return device;
    }

    @Override
    public void onPeersDiscovered(Collection<Device> devices) {
        this.devices = devices;
        this.notifyDataSetChanged();
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        final TextView text;
        final RelativeLayout body;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            this.text = itemView.findViewById(R.id.text);
            this.body = itemView.findViewById(R.id.body);
        }
    }
}
