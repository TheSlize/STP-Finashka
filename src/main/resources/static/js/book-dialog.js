const dialog = document.getElementById('book-dialog');
const fields = ['title', 'publishingHouse', 'studentName', 'issueDate', 'returnDate'];

document.addEventListener('click', event => {
    const button = event.target.closest('[data-action]');
    if (!button) {
        return;
    }
    const data = button.dataset;
    const form = dialog.querySelector('form');
    form.action = data.action;
    dialog.querySelector('h2').textContent = data.heading;
    fields.forEach(name => form.elements[name].value = data[name] || '');
    form.querySelectorAll('.error').forEach(error => error.remove());
    form.querySelectorAll('.invalid').forEach(input => input.classList.remove('invalid'));
    dialog.showModal();
});

if (dialog.dataset.open) {
    dialog.showModal();
}
