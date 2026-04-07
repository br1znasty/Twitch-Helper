export function navigate(path) {
    window.location.hash = path;
}

export function getRoute() {
    return window.location.hash || '#/';
}

export function render(app, content) {
    app.innerHTML = content;
}
