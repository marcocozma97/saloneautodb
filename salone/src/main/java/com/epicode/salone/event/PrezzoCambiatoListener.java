package com.epicode.salone.event;

import com.epicode.salone.entity.Avviso;
import com.epicode.salone.repository.AvvisoRepository;
import com.epicode.salone.service.EmailService;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
public class PrezzoCambiatoListener {

    private static final Logger log = LoggerFactory.getLogger(PrezzoCambiatoListener.class);

    private final AvvisoRepository avvisoRepository;
    private final EmailService emailService;

    public PrezzoCambiatoListener(AvvisoRepository avvisoRepository, EmailService emailService) {
        this.avvisoRepository = avvisoRepository;
        this.emailService = emailService;
    }

    // AFTER_COMMIT: parte solo se il nuovo prezzo è stato davvero salvato.
    // @Async: parte in un altro thread
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void quandoCambiaIlPrezzo(PrezzoCambiatoEvent evento) {

        List<Avviso> avvisi = avvisoRepository.trovaAvvisiAttraversati(
                evento.autoId(), evento.vecchioPrezzo(), evento.nuovoPrezzo());

        for (Avviso avviso : avvisi) {

            int righeAggiornate = avvisoRepository.segnaComeInviato(avviso.getId());
            if (righeAggiornate != 1) {
                continue;
            }

            try {
                emailService.inviaAvvisoPrezzo(avviso, evento.nuovoPrezzo());
                // nel log: id dell'avviso e dell'auto
                log.info("Mail inviata per avviso {} (auto {})", avviso.getId(), evento.autoId());
            } catch (MessagingException | MailException e) {
                // se Gmail non risponde, l'avviso RESTA inviato e la mail è persa.
                log.error("Invio mail fallito per avviso {} (auto {}): {}",
                        avviso.getId(), evento.autoId(), e.getClass().getSimpleName());
            }
        }
    }
}