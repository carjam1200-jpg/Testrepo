// Simple To-Do app using localStorage
(() => {
  const STORAGE_KEY = 'todos-v1';

  // DOM elements
  const form = document.getElementById('todo-form');
  const input = document.getElementById('todo-input');
  const listEl = document.getElementById('todo-list');
  const countEl = document.getElementById('count');
  const clearCompletedBtn = document.getElementById('clear-completed');
  const filterButtons = document.querySelectorAll('.filter-btn');

  // App state
  let todos = []; // { id, text, completed, createdAt }
  let filter = 'all'; // all | active | completed

  // Utility helpers
  const uid = () => Date.now().toString(36) + Math.random().toString(36).slice(2,8);

  // Load & Save
  function loadTodos(){
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      todos = raw ? JSON.parse(raw) : [];
    } catch (e) {
      console.error('Failed to parse todos from storage', e);
      todos = [];
    }
  }

  function saveTodos(){
    localStorage.setItem(STORAGE_KEY, JSON.stringify(todos));
  }

  // Rendering
  function render(){
    // filter
    let visible = todos;
    if (filter === 'active') visible = todos.filter(t => !t.completed);
    else if (filter === 'completed') visible = todos.filter(t => t.completed);

    // Clear list and render items
    listEl.innerHTML = '';
    if (visible.length === 0) {
      const empty = document.createElement('li');
      empty.className = 'todo-item';
      empty.textContent = 'No tasks';
      listEl.appendChild(empty);
    } else {
      visible.forEach(todo => {
        const li = document.createElement('li');
        li.className = 'todo-item';
        li.dataset.id = todo.id;

        // checkbox
        const cbWrap = document.createElement('label');
        cbWrap.className = 'todo-checkbox';
        const cb = document.createElement('input');
        cb.type = 'checkbox';
        cb.checked = todo.completed;
        cb.setAttribute('aria-label', 'Mark todo complete');
        cb.addEventListener('change', () => toggleComplete(todo.id));
        cbWrap.appendChild(cb);

        // text (inline edit)
        const text = document.createElement('div');
        text.className = 'todo-text' + (todo.completed ? ' completed' : '');
        text.tabIndex = 0;
        text.textContent = todo.text;
        text.title = 'Double-click or press Enter to edit';
        // editing on double-click or Enter
        text.addEventListener('dblclick', () => startEdit(todo.id, text));
        text.addEventListener('keydown', (e) => {
          if (e.key === 'Enter') { e.preventDefault(); startEdit(todo.id, text); }
        });

        // actions
        const actions = document.createElement('div');
        actions.className = 'item-actions';
        const editBtn = document.createElement('button');
        editBtn.className = 'icon-btn';
        editBtn.innerHTML = '✎';
        editBtn.title = 'Edit';
        editBtn.addEventListener('click', () => startEdit(todo.id, text));

        const delBtn = document.createElement('button');
        delBtn.className = 'icon-btn delete';
        delBtn.innerHTML = '🗑';
        delBtn.title = 'Delete';
        delBtn.addEventListener('click', () => deleteTodo(todo.id));

        actions.appendChild(editBtn);
        actions.appendChild(delBtn);

        li.appendChild(cbWrap);
        li.appendChild(text);
        li.appendChild(actions);
        listEl.appendChild(li);
      });
    }

    // count
    const remaining = todos.filter(t => !t.completed).length;
    countEl.textContent = `${remaining} item${remaining !== 1 ? 's' : ''} left`;

    // update filter button styles / aria-selected
    filterButtons.forEach(btn => {
      const f = btn.dataset.filter;
      if (f === filter) {
        btn.classList.add('active');
        btn.setAttribute('aria-selected', 'true');
      } else {
        btn.classList.remove('active');
        btn.setAttribute('aria-selected', 'false');
      }
    });
  }

  // Actions
  function addTodo(text){
    const trimmed = (text || '').trim();
    if (!trimmed) return;
    const todo = { id: uid(), text: trimmed, completed: false, createdAt: Date.now() };
    todos.unshift(todo); // newest on top
    saveTodos();
    render();
  }

  function toggleComplete(id){
    const t = todos.find(x => x.id === id);
    if (!t) return;
    t.completed = !t.completed;
    saveTodos();
    render();
  }

  function deleteTodo(id){
    todos = todos.filter(t => t.id !== id);
    saveTodos();
    render();
  }

  function startEdit(id, textNode){
    const todo = todos.find(t => t.id === id);
    if (!todo) return;

    // create input
    const input = document.createElement('input');
    input.type = 'text';
    input.className = 'todo-input';
    input.value = todo.text;
    input.style.minWidth = '160px';

    // replace textNode with input
    const parent = textNode.parentElement;
    parent.replaceChild(input, textNode);
    input.focus();
    input.select();

    function finish(save){
      if (save) {
        const val = input.value.trim();
        if (val) {
          todo.text = val;
        } else {
          // if emptied, delete
          todos = todos.filter(t => t.id !== id);
        }
      }
      saveTodos();
      render();
    }

    input.addEventListener('blur', () => finish(true));
    input.addEventListener('keydown', (e) => {
      if (e.key === 'Enter') finish(true);
      else if (e.key === 'Escape') finish(false);
    });
  }

  function clearCompleted(){
    todos = todos.filter(t => !t.completed);
    saveTodos();
    render();
  }

  // init
  function setup(){
    loadTodos();
    render();

    // form submit
    form.addEventListener('submit', (e) => {
      e.preventDefault();
      addTodo(input.value);
      input.value = '';
      input.focus();
    });

    clearCompletedBtn.addEventListener('click', clearCompleted);

    filterButtons.forEach(btn => {
      btn.addEventListener('click', () => {
        filter = btn.dataset.filter;
        render();
      });
    });

    // keyboard shortcut: 'n' to focus input
    window.addEventListener('keydown', (e) => {
      if (e.key === 'n' && document.activeElement.tagName !== 'INPUT' && document.activeElement.tagName !== 'TEXTAREA') {
        input.focus();
        input.select && input.select();
      }
    });

    // storage event: stay in sync between tabs
    window.addEventListener('storage', (e) => {
      if (e.key === STORAGE_KEY) {
        loadTodos();
        render();
      }
    });
  }

  setup();
})();
