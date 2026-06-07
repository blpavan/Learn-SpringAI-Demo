package Learn_SpringAI.KnowledgeDesk.rag;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SampleDataLoader {

    // This is step 1 of RAG (Indexing)

    private final VectorStore vectorStore;

    public SampleDataLoader(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    private void loadDataIntoVectorStore() {

        List<Document> documents = getSampleData().stream()
                                    .map(Document::new)
                                    .toList();

        vectorStore.add(documents);

    }

    private List<String> getSampleData() {
        return List.of(
                // Technology
                "Java is a platform-independent programming language widely used for enterprise applications.",
                "Spring Framework provides comprehensive infrastructure support for Java applications.",
                "Spring Boot simplifies application setup through auto-configuration and starter dependencies.",
                "Microservices architecture breaks applications into small independently deployable services.",
                "Apache Kafka is a distributed event streaming platform used for real-time data processing.",
                "Docker packages applications and dependencies into lightweight containers.",
                "Kubernetes automates deployment, scaling, and management of containerized applications.",
                "Redis is an in-memory data store commonly used for caching and session management.",
                "MySQL is a popular open-source relational database management system.",
                "MongoDB is a NoSQL database designed for handling flexible document-based data.",
                "REST APIs allow systems to communicate over HTTP using standard methods.",
                "GraphQL enables clients to request exactly the data they need from APIs.",
                "Vector databases store embeddings for semantic search and AI-powered retrieval.",
                "Retrieval Augmented Generation combines search with large language models.",
                "Embeddings convert text into numerical vectors that capture semantic meaning.",
                "CI/CD pipelines automate software building, testing, and deployment processes.",
                "Azure DevOps provides tools for source control, build automation, and deployments.",
                "Git is a distributed version control system widely used in software development.",
                "System design focuses on building scalable, reliable, and maintainable applications.",
                "Cloud computing enables on-demand access to computing resources over the internet.",

                // Artificial Intelligence
                "Artificial Intelligence enables machines to perform tasks that typically require human intelligence.",
                "Machine Learning allows systems to learn patterns from data without explicit programming.",
                "Deep Learning uses neural networks with multiple layers to solve complex problems.",
                "Large Language Models are trained on massive datasets to generate human-like text.",
                "Prompt engineering is the process of designing effective inputs for AI systems.",
                "Generative AI can create text, images, audio, and code.",
                "Natural Language Processing helps computers understand and generate human language.",
                "Computer vision enables machines to analyze and interpret visual information.",
                "Fine-tuning adapts a pre-trained model for specific tasks.",
                "Hallucination occurs when an AI system generates incorrect information confidently.",

                // Share Market
                "The stock market allows investors to buy and sell ownership in publicly traded companies.",
                "Stocks represent partial ownership in a company.",
                "The Nifty 50 tracks fifty major companies listed on the National Stock Exchange of India.",
                "The Sensex tracks thirty leading companies listed on the Bombay Stock Exchange.",
                "A bull market is characterized by rising stock prices and positive investor sentiment.",
                "A bear market is characterized by falling stock prices and negative investor sentiment.",
                "Investors diversify portfolios to reduce investment risk.",
                "Fundamental analysis evaluates companies based on financial performance and business strength.",
                "Technical analysis studies historical price movements and trading volume.",
                "Dividend-paying stocks provide regular income to shareholders.",
                "Mutual funds pool money from multiple investors to invest in diversified assets.",
                "Exchange Traded Funds or ETFs trade on stock exchanges like regular shares.",
                "Market capitalization represents the total value of a company's outstanding shares.",
                "Long-term investing often benefits from the power of compounding.",
                "Risk and return are generally correlated in financial markets.",

                // Startup Ecosystem
                "A startup is a company designed to grow rapidly through innovation and scalability.",
                "Entrepreneurs identify problems and build businesses to solve them.",
                "Venture capital firms invest in high-growth startups.",
                "Angel investors provide funding during the early stages of a startup.",
                "Unicorn startups are privately held companies valued at over one billion dollars.",
                "India has one of the world's largest startup ecosystems.",
                "Bangalore is often called the Silicon Valley of India.",
                "Startup founders focus on achieving product-market fit.",
                "Bootstrapping refers to building a company without external funding.",
                "Pitch decks are presentations used to attract investors.",
                "Successful startups prioritize customer feedback and rapid iteration.",
                "Many startups use cloud platforms to reduce infrastructure costs.",

                // Economy
                "Inflation is the sustained increase in the general price level of goods and services.",
                "Central banks use monetary policy to manage inflation and economic growth.",
                "Gross Domestic Product measures the value of goods and services produced within a country.",
                "Economic growth is often driven by productivity improvements and innovation.",
                "Interest rates influence borrowing, spending, and investment decisions.",
                "Unemployment rate measures the percentage of people actively seeking work.",
                "Fiscal policy involves government spending and taxation decisions.",
                "International trade allows countries to exchange goods and services.",
                "Foreign exchange markets facilitate currency trading worldwide.",
                "Recessions are periods of declining economic activity.",

                // War and Global Affairs
                "Geopolitical tensions can influence global trade and financial markets.",
                "International diplomacy helps resolve conflicts between nations.",
                "The United Nations promotes international cooperation and peace.",
                "Wars can disrupt supply chains and increase commodity prices.",
                "Defense spending often rises during periods of geopolitical uncertainty.",
                "Economic sanctions are restrictions imposed to influence another country's behavior.",
                "Energy security is an important concern for many nations.",
                "Global conflicts can affect investor confidence and economic growth.",
                "Military alliances strengthen cooperation between participating countries.",
                "Cyber warfare has become a significant aspect of modern conflicts.",

                // Cricket
                "Cricket is one of the most popular sports in India and several other countries.",
                "The International Cricket Council governs international cricket.",
                "India won the Cricket World Cup in 1983 and 2011.",
                "Sachin Tendulkar scored one hundred international centuries during his career.",
                "Virat Kohli is regarded as one of the finest modern batsmen.",
                "MS Dhoni captained India to multiple ICC tournament victories.",
                "The Indian Premier League is among the world's most valuable sports leagues.",
                "Test cricket is considered the longest and most traditional format of the game.",
                "One Day Internationals consist of fifty overs per side.",
                "Twenty20 cricket is a fast-paced format consisting of twenty overs per side.",
                "Rohit Sharma holds several records in limited-overs cricket.",
                "Jasprit Bumrah is known for his exceptional fast bowling skills.",

                // Space and Science
                "The Earth revolves around the Sun once every approximately 365 days.",
                "The Moon is Earth's only natural satellite.",
                "NASA is the space agency of the United States.",
                "ISRO is India's national space research organization.",
                "Mars is often called the Red Planet.",
                "The James Webb Space Telescope studies distant galaxies and celestial objects.",
                "Gravity is the force that attracts objects toward each other.",
                "Water exists in solid, liquid, and gaseous states.",
                "The speed of light in a vacuum is approximately 299,792 kilometers per second.",
                "Albert Einstein developed the theory of relativity.",

                // Health and Fitness
                "Regular exercise improves cardiovascular health and overall well-being.",
                "A balanced diet includes proteins, carbohydrates, fats, vitamins, and minerals.",
                "Walking is one of the simplest forms of physical activity.",
                "Adequate sleep is important for mental and physical health.",
                "Hydration helps maintain normal body functions.",
                "Strength training improves muscle mass and bone density.",
                "Yoga combines physical postures, breathing exercises, and meditation.",
                "Mental health is an essential component of overall wellness.",
                "Preventive healthcare focuses on reducing disease risk.",
                "Consistent healthy habits often produce long-term benefits.",

                // General Knowledge
                "Renewable energy includes solar, wind, and hydroelectric power.",
                "Cybersecurity protects systems and data from unauthorized access.",
                "Blockchain is a distributed ledger technology.",
                "The internet connects billions of devices worldwide.",
                "Electric vehicles use electric motors instead of internal combustion engines.",
                "Education plays a crucial role in economic and social development.",
                "Digital transformation involves integrating technology into business processes.",
                "Data analytics helps organizations make informed decisions.",
                "Sustainability focuses on meeting present needs without compromising future generations.",
                "Communication skills are essential in professional environments.",

                // Java Techie
                "Java Techie is a YouTube channel focused on Java, Spring Boot, Microservices, and System Design.",
                "Java Techie provides tutorials on Kafka, Kubernetes, Docker, and Spring Security.",
                "The Java Techie channel helps developers prepare for software engineering interviews.",
                "Basant is the creator of the Java Techie YouTube channel.",
                "Java Techie regularly publishes hands-on coding demonstrations.",
                "Many developers use Java Techie content to learn backend development concepts.",
                "Java Techie explains distributed systems using practical examples.",
                "Spring Boot tutorials are among the most popular topics covered by Java Techie.",
                "Java Techie also teaches cloud-native application development.",
                "The channel covers both beginner and advanced Java concepts."
        );
    }
}
