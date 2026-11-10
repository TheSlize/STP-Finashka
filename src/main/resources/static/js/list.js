async function load(url, options) {
    const response = await fetch(url, options);
    // если сессия истекла, сервер отдаёт страницу входа - тогда просто перезагружаемся и попадаем на неё
    if (!response.ok || new URL(response.url).pathname === '/login') {
        location.reload();
        return;
    }
    const page = new DOMParser().parseFromString(await response.text(), 'text/html');
    document.querySelector('main').innerHTML = page.querySelector('main').innerHTML;

    const equipmentDialog = document.getElementById('equipment-dialog');
    const loadedDialog = page.getElementById('equipment-dialog');
    equipmentDialog.innerHTML = loadedDialog.innerHTML;
    if (!loadedDialog.dataset.open) {
        equipmentDialog.close();
    } else if (!equipmentDialog.open) {
        equipmentDialog.showModal();
    }

    if (response.url !== location.href) {
        history.pushState(null, '', response.url);
    }
}

document.addEventListener('submit', event => {
    const button = event.submitter;
    // форма выхода в шапке отправляется обычным способом
    if (event.defaultPrevented || !event.target.closest('main, dialog')
            || (button && button.getAttribute('formmethod') === 'dialog')) {
        return;
    }
    event.preventDefault();
    const data = new URLSearchParams(new FormData(event.target));
    if (event.target.method === 'post') {
        load(event.target.action, {method: 'POST', body: data});
    } else {
        load(event.target.action + '?' + data);
    }
});

document.addEventListener('click', event => {
    const link = event.target.closest('main a');
    if (link) {
        event.preventDefault();
        load(link.href);
    }
});

window.addEventListener('popstate', () => load(location.href));
