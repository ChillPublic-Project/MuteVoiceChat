package fr.redsavant;

import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EntitySoundPacketEvent;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.LocationalSoundPacketEvent;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import de.maxhenkel.voicechat.api.events.StaticSoundPacketEvent;
import de.maxhenkel.voicechat.api.VoicechatConnection;

import java.util.UUID;

public class VoicePlugin implements VoicechatPlugin {

    @Override
    public String getPluginId() {
        return "litebans-voice-mute";
    }

    @Override
    public void initialize(de.maxhenkel.voicechat.api.VoicechatApi api) {
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(MicrophonePacketEvent.class, event -> {
            VoicechatConnection connection = event.getSenderConnection();
            if (connection == null) return;
            if (Main.getInstance().isMuted(connection.getPlayer().getUuid())) {
                event.cancel();
            }
        });

        registration.registerEvent(EntitySoundPacketEvent.class, event -> {
            VoicechatConnection sender = event.getSenderConnection();
            if (sender != null && Main.getInstance().isMuted(sender.getPlayer().getUuid())) {
                event.cancel();
            }
        });

        registration.registerEvent(LocationalSoundPacketEvent.class, event -> {
            VoicechatConnection sender = event.getSenderConnection();
            if (sender != null && Main.getInstance().isMuted(sender.getPlayer().getUuid())) {
                event.cancel();
            }
        });

        registration.registerEvent(StaticSoundPacketEvent.class, event -> {
            VoicechatConnection sender = event.getSenderConnection();
            if (sender != null && Main.getInstance().isMuted(sender.getPlayer().getUuid())) {
                event.cancel();
            }
        });
    }
}