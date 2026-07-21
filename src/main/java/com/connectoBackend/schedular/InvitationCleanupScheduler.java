package com.connectoBackend.schedular;

import com.connectoBackend.chat.enums.InvitationStatus;
import com.connectoBackend.chat.repository.GroupInvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class InvitationCleanupScheduler {

	private final GroupInvitationRepository groupInvitationRepository;

	@Scheduled(cron = "0 0 * * * *")
	public void cleanupExpiredInvitations() {
		groupInvitationRepository.findAll().stream()
				.filter(invitation -> invitation.getStatus() == InvitationStatus.PENDING)
				.filter(invitation -> invitation.getExpiresAt().isBefore(LocalDateTime.now()))
				.forEach(invitation -> invitation.setStatus(InvitationStatus.EXPIRED));
		groupInvitationRepository.saveAll(groupInvitationRepository.findAll().stream()
				.filter(invitation -> invitation.getStatus() == InvitationStatus.EXPIRED)
				.toList());
	}
}