import { renderHomePage } from "../pages/homePage";
import { renderLoginPage } from "../pages/loginPage";
import { renderRegisterPage } from "../pages/registerPage";
import { renderSettingsPage } from "../pages/settingsPage";
import { renderWelcomePage } from "../pages/welcomePage";
import { getRoute } from "../utils/navigation";

export function initRouter() {
    const app = document.getElementById("app");

    function router() {
        const route = getRoute();

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

            default:
                renderWelcomePage(app);
                break;
        }
    }

    window.addEventListener("hashchange", router);
    window.addEventListener("load", router);

    router();

}