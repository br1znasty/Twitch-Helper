import { renderHomePage } from "../pages/homePage";
import { renderLoginPage } from "../pages/loginPage";
import { renderRegisterPage } from "../pages/registerPage";
import { renderSettingsPage } from "../pages/settingsPage";
import { renderWelcomePage } from "../pages/welcomePage";
import { renderWidgetsPage } from "../pages/widgetsPage";
import { getRoute } from "../utils/navigation";

export function initRouter() {
    const app = document.getElementById("app");

    function router() {
        const route = getRoute();
        document.body.classList.remove("overlay-body");

        switch (route) {
            case "#/register":
                renderRegisterPage(app);
                break;

            case "#/login":
                renderLoginPage(app);
                break;

            case "#/home":
                renderHomePage(app);
                break;

            case "#/settings":
                renderSettingsPage(app);
                break;

            case "#/widgets":
                renderWidgetsPage(app);
                break;

            default:
                renderWelcomePage(app);
                break;
        }
    }

    window.addEventListener("hashchange", router);
    window.addEventListener("load", router);

    router();
}
