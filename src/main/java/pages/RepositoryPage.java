package pages;

import factory.DriverFactory;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.LoggerUtil;
import utils.WaitUtil;

import java.util.List;

public class RepositoryPage {

    private static final Logger logger = LoggerUtil.getLogger(RepositoryPage.class);

    private WebDriver driver;

    public RepositoryPage() {
        this.driver = DriverFactory.getDriver();
    }

    public void openFirstRepository() {
        logger.info("[STEP] Finding repository links on topic page...");
        WaitUtil.waitForPageReady();

        String repoUrl = findFirstRepositoryUrl();

        if (repoUrl != null) {
            String fullRepoUrl = "https://github.com" + repoUrl;
            logger.info("[ACTION] Opening repository: {}", fullRepoUrl);
            DriverFactory.getDriver().navigate().to(fullRepoUrl);
            WaitUtil.waitForUrlContains(repoUrl);
            WaitUtil.waitForPageReady();
            logger.info("[SUCCESS] Repository page loaded successfully");
        } else {
            throw new IllegalStateException("No repository URL candidate found.");
        }
    }

    private String findFirstRepositoryUrl() {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        String script = """
                const blocked = new Set([
                  'about', 'account', 'apps', 'blog', 'codespaces', 'collections', 'contact',
                  'customer-stories', 'dashboard', 'enterprise', 'events', 'explore', 'features',
                  'github-copilot', 'issues', 'join', 'login', 'marketplace', 'mobile', 'new',
                  'notifications', 'organizations', 'orgs', 'pricing', 'pulls', 'readme',
                  'resources', 'search', 'security', 'settings', 'site', 'solutions',
                  'sponsors', 'team', 'topics', 'trending'
                ]);
                const anchors = Array.from(document.querySelectorAll('a[href]'));
                for (const anchor of anchors) {
                  const href = new URL(anchor.getAttribute('href'), 'https://github.com').href;
                  if (!href.startsWith('https://github.com/')) continue;
                  const url = new URL(href);
                  const parts = url.pathname.split('/').filter(Boolean);
                  if (parts.length !== 2) continue;
                  if (blocked.has(parts[0].toLowerCase()) || blocked.has(parts[1].toLowerCase())) continue;
                  if (parts[0].includes('.') || parts[1].includes('.')) continue;
                  return '/' + parts[0] + '/' + parts[1];
                }
                return null;
                """;

        Object result = js.executeScript(script);
        if (result instanceof String repoPath && repoPath.matches("^/[^/#?]+/[^/#?]+$")) {
            logger.info("[CANDIDATE 1] https://github.com{}", repoPath);
            return repoPath;
        }

        List<WebElement> links = driver.findElements(By.cssSelector("a[href^='/'], a[href^='https://github.com/']"));
        logger.info("[INFO] Total links scanned by fallback: {}", links.size());
        for (WebElement link : links) {
            String candidatePath = normalizeRepositoryPath(link.getAttribute("href"));
            if (candidatePath != null) {
                logger.info("[CANDIDATE 1] https://github.com{}", candidatePath);
                return candidatePath;
            }
        }

        return null;
    }

    private String normalizeRepositoryPath(String href) {
        if (href == null || href.isBlank()) {
            return null;
        }

        String candidatePath = href;
        if (href.startsWith("https://github.com/")) {
            candidatePath = href.substring("https://github.com".length());
        }

        if (!candidatePath.matches("^/[^/#?]+/[^/#?]+$")) {
            return null;
        }

        String[] parts = candidatePath.substring(1).split("/");
        String owner = parts[0].toLowerCase();
        String repository = parts[1].toLowerCase();
        List<String> blocked = List.of(
                "about", "account", "apps", "blog", "codespaces", "collections", "contact",
                "customer-stories", "dashboard", "enterprise", "events", "explore", "features",
                "github-copilot", "issues", "join", "login", "marketplace", "mobile", "new",
                "notifications", "organizations", "orgs", "pricing", "pulls", "readme",
                "resources", "search", "security", "settings", "site", "solutions",
                "sponsors", "team", "topics", "trending");

        if (blocked.contains(owner) || blocked.contains(repository)
                || owner.contains(".") || repository.contains(".")) {
            return null;
        }

        return candidatePath;
    }

    public String getRepositoryName() {
        try {
            return DriverFactory.getDriver()
                    .findElement(By.cssSelector("strong[itemprop='name'] a, [data-testid='repository-name']"))
                    .getText();
        } catch (Exception e) {
            String title = DriverFactory.getDriver().getTitle();
            return title.replace("GitHub - ", "")
                    .split(":")[0];
        }
    }

    public String getDescription() {
        try {
            return DriverFactory.getDriver()
                    .findElement(By.xpath("//p[contains(@class,'f4')]") )
                    .getText();
        } catch (Exception e) {
            return "Description Not Found";
        }
    }

    public String getLanguage() {
        try {
            return DriverFactory.getDriver()
                    .findElement(By.xpath("//span[@itemprop='programmingLanguage']"))
                    .getText();
        } catch (Exception e) {
            try {
                Object language = ((JavascriptExecutor) driver).executeScript("""
                        const direct = document.querySelector('[itemprop="programmingLanguage"]');
                        if (direct && direct.textContent.trim()) return direct.textContent.trim();

                        const links = Array.from(document.querySelectorAll('a[href*="search?l="], a[href*="?l="]'));
                        for (const link of links) {
                          const text = link.textContent.replace(/\\s+/g, ' ').trim();
                          const match = text.match(/[A-Za-z][A-Za-z0-9+#. -]*/);
                          if (match) return match[0].trim();
                        }
                        return null;
                        """);
                if (language instanceof String value && !value.isBlank()) {
                    return value.replaceAll("\\s+\\d+(\\.\\d+)?%?$", "").trim();
                }
            } catch (Exception ignored) {
                return "Language Not Found";
            }
            return "Language Not Found";
        }
    }

    public String getStars() {
        try {
            return DriverFactory.getDriver()
                    .findElement(By.xpath("//a[contains(@href,'stargazers')]"))
                    .getText();
        } catch (Exception e) {
            return "0";
        }
    }

    public String getForks() {
        try {
            return DriverFactory.getDriver()
                    .findElement(By.xpath("//a[contains(@href,'forks')]"))
                    .getText();
        } catch (Exception e) {
            return "0";
        }
    }
}
