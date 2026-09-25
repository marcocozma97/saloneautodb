package com.epicode.salone.service;

import com.epicode.salone.entity.Auto;
import com.epicode.salone.entity.Avviso;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.Locale;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String mittente;
    private final String frontendUrl;

    public EmailService(JavaMailSender mailSender,
                        @Value("${spring.mail.username}") String mittente,
                        @Value("${app.cors.allowed-origin}") String frontendUrl) {
        this.mailSender = mailSender;
        this.mittente = mittente;
        this.frontendUrl = frontendUrl;
    }

    public void inviaAvvisoPrezzo(Avviso avviso, Integer nuovoPrezzo) throws MessagingException {
        Auto auto = avviso.getAuto();

        // ESCAPE DI OGNI VALORE (slide 5): nessun testo dell'utente entra nell'HTML così com'è
        String nome = HtmlUtils.htmlEscape(avviso.getUtente().getNome());
        String nomeAuto = HtmlUtils.htmlEscape(auto.getMarca() + " " + auto.getModello());
        String prezzo = formattaEuro(nuovoPrezzo);
        String soglia = formattaEuro(avviso.getSoglia());

        // il link di disattivazione usa il token casuale, non l'id (slide 6)
        String linkAuto = HtmlUtils.htmlEscape(frontendUrl + "/auto/" + auto.getId());
        String linkDisattiva = HtmlUtils.htmlEscape(
                frontendUrl + "/disattiva?token=" + avviso.getTokenDisattivazione());

        String html = """
                <html>
                  <body style="font-family: Arial, sans-serif; color: #222;">
                    <h2>Buone notizie, %s!</h2>
                    <p>Il prezzo di <strong>%s</strong> è sceso a <strong>%s</strong>.</p>
                    <p>La tua soglia era %s.</p>
                    <p><a href="%s">Guarda l'auto nel salone</a></p>
                    <hr>
                    <p style="font-size: 12px; color: #777;">
                      Hai ricevuto questa mail perché hai impostato un avviso di prezzo.
                      <a href="%s">Disattiva questo avviso</a>.
                    </p>
                  </body>
                </html>
                """.formatted(nome, nomeAuto, prezzo, soglia, linkAuto, linkDisattiva);

        MimeMessage messaggio = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(messaggio, "UTF-8");
        helper.setFrom(mittente);
        helper.setTo(avviso.getUtente().getEmail());
        // l'oggetto è testo semplice, non HTML
        helper.setSubject("Prezzo in calo: " + auto.getMarca() + " " + auto.getModello());
        helper.setText(html, true); // true = il contenuto è HTML

        mailSender.send(messaggio);
    }

    // 18000 -> "18.000 €"
    private String formattaEuro(Integer valore) {
        return String.format(Locale.ITALY, "%,d €", valore);
    }
}