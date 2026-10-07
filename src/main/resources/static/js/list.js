async function load(url, options) {
    const response = await fetch(url, options);
    if (!response.ok) {
        location.reload();
        return;
    }
    const page = new DOMParser().parseFromString(await response.text(), 'text/html');
    document.querySelector('main').innerHTML = page.querySelector('main').innerHTML;

    const bookDialog = document.getElementById('book-dialog');
    const loadedDialog = page.getElementById('book-dialog');
    bookDialog.innerHTML = loadedDialog.innerHTML;
    if (!loadedDialog.dataset.open) {
        bookDialog.close();
    } else if (!bookDialog.open) {
        bookDialog.showModal();
    }

    if (response.url !== location.href) {
        history.pushState(null, '', response.url);
    }
}

document.addEventListener('submit', event => {
    const button = event.submitter;
    if (event.defaultPrevented || (button && button.getAttribute('formmethod') === 'dialog')) {
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
