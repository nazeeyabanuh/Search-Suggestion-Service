const input = document.getElementById('searchInput');
const suggestionsEl = document.getElementById('suggestions');
const metaEl = document.getElementById('meta');
let debounceTimer;

input.addEventListener('input', () => {
  clearTimeout(debounceTimer);
  const query = input.value.trim();

  if (!query) {
    suggestionsEl.innerHTML = '';
    metaEl.textContent = '';
    return;
  }

  debounceTimer = setTimeout(() => fetchSuggestions(query), 200);
});

async function fetchSuggestions(query) {
  const start = performance.now();
  try {
    const res = await fetch(`/api/search?q=${encodeURIComponent(query)}`);
    const data = await res.json();
    const elapsed = (performance.now() - start).toFixed(0);

    renderSuggestions(data);
metaEl.textContent = `${data.length} result${data.length !== 1 ? 's' : ''} - ${elapsed}ms`;
  } catch (err) {
    metaEl.textContent = 'Error fetching suggestions';
    console.error(err);
  }
}

function renderSuggestions(words) {
  suggestionsEl.innerHTML = '';

  if (words.length === 0) {
    suggestionsEl.innerHTML = '<li class="empty">No matches found</li>';
    return;
  }

  words.forEach(word => {
    const li = document.createElement('li');
    li.textContent = word;
    li.addEventListener('click', () => {
      input.value = word;
      suggestionsEl.innerHTML = '';
    });
    suggestionsEl.appendChild(li);
  });
}