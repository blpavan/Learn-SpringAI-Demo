package Learn_SpringAI.KnowledgeDesk.service;

import Learn_SpringAI.KnowledgeDesk.model.KnowledgeArticle;
import Learn_SpringAI.KnowledgeDesk.repository.KnowledgeArticleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KnowledgeArticleService {

    private final KnowledgeArticleRepository knowledgeArticleRepository;

    public KnowledgeArticleService(KnowledgeArticleRepository knowledgeArticleRepository) {
        this.knowledgeArticleRepository = knowledgeArticleRepository;
    }

    public List<KnowledgeArticle> findAllArticles() {
        return knowledgeArticleRepository.findAll();
    }

    public void seedArticlesIfEmpty() {
        if (knowledgeArticleRepository.count() > 0) {
            return;
        }

        knowledgeArticleRepository.saveAll(getSampleArticles());
    }

    private List<KnowledgeArticle> getSampleArticles() {
        return List.of(
                new KnowledgeArticle(
                        "mongo-password-reset",
                        "Password reset from MongoDB",
                        "account-access",
                        "Users can reset passwords from the account security portal. The user must complete MFA before choosing a new password. Temporary reset links expire after 30 minutes."
                ),
                new KnowledgeArticle(
                        "mongo-vpn-certificate",
                        "VPN certificate troubleshooting from MongoDB",
                        "vpn",
                        "If the VPN client shows CERT_EXPIRED, collect the device name, asset tag, and username. Escalate the ticket to endpoint engineering for certificate renewal."
                ),
                new KnowledgeArticle(
                        "mongo-software-license",
                        "Software license request from MongoDB",
                        "software",
                        "Approved software licenses require manager approval and available seats. If seats are unavailable, route the request to procurement with business justification."
                ),
                new KnowledgeArticle(
                        "mongo-incident-escalation",
                        "Incident escalation rules from MongoDB",
                        "incident-management",
                        "Escalate incidents immediately when more than 20 users are affected, payroll is blocked, customer production systems are impacted, or data loss is suspected."
                )
        );
    }
}
