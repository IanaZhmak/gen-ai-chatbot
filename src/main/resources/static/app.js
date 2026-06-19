// ─────────────────────────────────────────────────
// RAG Demo — app.js
// ─────────────────────────────────────────────────
const API = '';  // same origin

// ─── State ───────────────────────────────────────
let currentChatId = null;
let chatModels = [];
let pendingFile = null;

// ─── DOM refs ────────────────────────────────────
const $ = id => document.getElementById(id);
const chatList        = $('chat-list');
const messagesArea    = $('messages-area');
const inputText       = $('input-text');
const btnSend         = $('btn-send');
const modelSelect     = $('model-select');
const chatView        = $('chat-view');
const emptyState      = $('empty-state');
const chatTitleDisplay= $('chat-title-display');
const sourcesList     = $('sources-list');
const sourcesCount    = $('sources-count');
const modalOverlay    = $('modal-overlay');

//-----NEW----
const modeDisplay     = $('mode-display');
const modeDescription = $('chat-mode-description');
const sourcesBar      = $('sources-bar');

// ─── Init ─────────────────────────────────────────
(async function init() {
  createToastContainer();
  await loadChatModels();
  await loadChatList();
  bindEvents();
})();

// ─── API helpers ──────────────────────────────────
async function apiFetch(path, opts = {}) {
  const headers = { ...(opts.headers || {}) };

    if (!(opts.body instanceof FormData) && !headers['Content-Type']) {
      headers['Content-Type'] = 'application/json';
    }

  const res = await fetch(API + path, {
      ...opts,
      headers
    });

  if (!res.ok) {
    const msg = await res.text().catch(() => res.statusText);
    throw new Error(msg || `HTTP ${res.status}`);
  }
  const ct = res.headers.get('content-type') || '';
  return ct.includes('application/json') ? res.json() : res.text();
}

// ─── Models ───────────────────────────────────────
async function loadChatModels() {
  try {
    chatModels = await apiFetch('/api/models/chat');
//    populateModelSelect(modelSelect);
    populateModelSelect($('new-chat-model'));
  } catch (e) {
    console.warn('Could not load chat models', e);
  }
}

function populateModelSelect(sel) {
  if (!sel) return;
  sel.innerHTML = '';
  chatModels.forEach(m => {
    const opt = document.createElement('option');
    opt.value = m.key;
    opt.textContent = `${m.mode}`;
    sel.appendChild(opt);
  });
}

// ─── Chat list ────────────────────────────────────
async function loadChatList() {
  try {
    const chats = await apiFetch('/api/chats');
    renderChatList(chats);
  } catch (e) {
    showToast('Failed to load chats', 'error');
  }
}

function renderChatList(chats) {
  chatList.innerHTML = '';

  chats.forEach(chat => {
    const el = document.createElement('div');
    el.className = 'chat-item' + (chat.id === currentChatId ? ' active' : '');
    el.dataset.chatId = chat.id;

    const title = document.createElement('span');
    title.className = 'chat-title';
    title.textContent = chat.title;
    title.addEventListener('click', () => openChat(chat));

    const deleteBtn = document.createElement('button');
    deleteBtn.className = 'chat-delete-btn';
    deleteBtn.innerHTML = '🗑️';
    deleteBtn.title = 'Delete chat';

    deleteBtn.addEventListener('click', async (e) => {
      e.stopPropagation();

      const confirmed = confirm(`Delete chat "${chat.title}"?`);
      if (!confirmed) return;

      try {
        await apiFetch(`/api/chats/${chat.id}`, {
          method: 'DELETE'
        });

        showToast('Chat deleted', 'success');

        if (currentChatId === chat.id) {
          currentChatId = null;
          messagesArea.innerHTML = '';
        }

        await loadChatList();
      } catch (err) {
        showToast('Failed to delete chat', 'error');
      }
    });

    el.appendChild(title);
    el.appendChild(deleteBtn);
    chatList.appendChild(el);
  });
}

// ─── Open / create chat ───────────────────────────
async function openChat(chat) {
  currentChatId = chat.id;
  chatTitleDisplay.textContent = chat.title;
//  modelSelect.value = chat.modeKey;
updateModeUi(chat.modeKey);

  emptyState.classList.add('hidden');
  chatView.classList.remove('hidden');

  // Highlight active
  document.querySelectorAll('.chat-item').forEach(el => {
    el.classList.toggle('active', Number(el.dataset.chatId) === currentChatId);
  });

//  await Promise.all([loadMessages(), loadSources()]);
await loadMessages();

const normalizedMode = normalizeModeKey(chat.modeKey);

if (normalizedMode === 'RAGCHAT') {
  await loadSources();
} else {
  sourcesCount.textContent = '0';
  sourcesList.innerHTML = '';
}
}

function normalizeModeKey(modeKey) {
  if (!modeKey) return '';
  return String(modeKey).replace(/[-_ ]/g, '').toUpperCase();
}

function updateModeUi(modeKey) {
  const normalized = normalizeModeKey(modeKey);

  const descriptions = {
    SIMPLECHAT: 'Simple Chat — regular conversation with AI only. No RAG, no memory, no tools.',
    PROMPTBASEDCHAT: 'Prompt Based Chat — response follows a predefined role and prompt structure.',
    RAGCHAT: 'RAG Chat — answers are based on retrieved document context.',
    TOOLSCHAT: 'Tools Chat — the assistant can call external tools or APIs.',
    MEMORYCHAT: 'Memory Chat — the assistant remembers previous messages in the same conversation.'
  };

  const labels = {
    SIMPLECHAT: 'Simple Chat',
    PROMPTBASEDCHAT: 'Prompt Based Chat',
    RAGCHAT: 'RAG Chat',
    TOOLSCHAT: 'Tools Chat',
    MEMORYCHAT: 'Memory Chat'
  };

  if (modeDisplay) {
    modeDisplay.textContent = labels[normalized] || modeKey;
  }

  if (modeDescription) {
    modeDescription.textContent = descriptions[normalized] || '';
  }

//if (modeDescription) {
//  modeDescription.innerHTML = descriptions[modeKey] || '';
//}

  if (sourcesBar) {
    if (normalized === 'RAGCHAT') {
      sourcesBar.classList.remove('hidden');
    } else {
      sourcesBar.classList.add('hidden');
    }
  }
}

async function createChat(title, modeKey) {
  try {
    const chat = await apiFetch('/api/chats', {
      method: 'POST',
      body: JSON.stringify({ title, modeKey })
    });
    await loadChatList();
    openChat(chat);
    return chat;
  } catch (e) {
    showToast('Failed to create chat: ' + e.message, 'error');
  }
}

// ─── Messages ─────────────────────────────────────
async function loadMessages() {
  if (!currentChatId) return;

  try {
    const msgs = await apiFetch(`/api/chats/${currentChatId}/messages`);
    messagesArea.innerHTML = '';

    msgs.forEach(m => {
      if (!m || !m.role || !m.text) return;

      appendMessage(
        m.role,
        m.text,
        null,
        false,
        m.fileName ? { fileName: m.fileName, fileUrl: m.fileUrl } : null
      );
    });

    scrollToBottom();
  } catch (e) {
    showToast('Failed to load messages', 'error');
  }
}

function appendMessage(role, text, chunks, streaming = false, attachment = null) {
  const wrap = document.createElement('div');
  wrap.className = `message ${role.toLowerCase()}`;

  const avatar = document.createElement('div');
  avatar.className = 'avatar';
  avatar.textContent = role === 'USER' ? '🧑' : '🤖';

  const right = document.createElement('div');

  const bubble = document.createElement('div');
  bubble.className = 'bubble' + (streaming ? ' streaming' : '');
  bubble.textContent = text;

  right.appendChild(bubble);

  if (attachment && attachment.fileName) {
      const fileRow = document.createElement('div');
      fileRow.className = 'message-file';

      if (attachment.fileUrl) {
        const link = document.createElement('a');
        link.className = 'message-file-link';
        link.href = attachment.fileUrl;
        link.textContent = `📎 ${attachment.fileName}`;
        link.setAttribute('download', attachment.fileName);
        fileRow.appendChild(link);
      } else {
        const span = document.createElement('span');
        span.className = 'message-file-link';
        span.textContent = `📎 ${attachment.fileName}`;
        fileRow.appendChild(span);
      }

      right.appendChild(fileRow);
    }

  // Show retrieved chunks toggle for assistant messages
  if (role === 'ASSISTANT' && chunks && chunks.length > 0) {
    const toggle = document.createElement('button');
    toggle.className = 'chunks-toggle';
    toggle.textContent = `📚 ${chunks.length} source chunk${chunks.length > 1 ? 's' : ''} used`;
    const chunksList = document.createElement('div');
    chunksList.className = 'chunks-list hidden';
    chunks.forEach(c => {
      const item = document.createElement('div');
      item.className = 'chunk-item';
      const src = document.createElement('div');
      src.className = 'chunk-source';
      src.textContent = [c.documentTitle, c.filePath, c.sourceType].filter(Boolean).join(' · ');
      const txt = document.createElement('div');
      txt.className = 'chunk-text';
      txt.textContent = c.text;
      item.appendChild(src);
      item.appendChild(txt);
      chunksList.appendChild(item);
    });
    toggle.addEventListener('click', () => chunksList.classList.toggle('hidden'));
    right.appendChild(toggle);
    right.appendChild(chunksList);
  }

  wrap.appendChild(avatar);
  wrap.appendChild(right);
  messagesArea.appendChild(wrap);
  scrollToBottom();
  return bubble;
}

function renderPendingAttachment() {
  const box = $('pending-attachment');
  if (!box) return;

  box.innerHTML = '';

  if (!pendingFile) {
    box.classList.add('hidden');
    return;
  }

  box.classList.remove('hidden');

  const item = document.createElement('div');
  item.className = 'pending-file';

  const icon = document.createElement('span');
  icon.textContent = '📎';

  const name = document.createElement('span');
  name.textContent = pendingFile.name;

  const removeBtn = document.createElement('button');
  removeBtn.textContent = '✕';
  removeBtn.className = 'pending-file-remove';
  removeBtn.addEventListener('click', () => {
    pendingFile = null;
    $('file-input').value = '';
    renderPendingAttachment();
  });

  item.appendChild(icon);
  item.appendChild(name);
  item.appendChild(removeBtn);
  box.appendChild(item);
}

//async function sendMessage() {
//  const question = inputText.value.trim();
//  if (!question || !currentChatId) return;
//
//  inputText.value = '';
//  inputText.style.height = 'auto';
//  btnSend.disabled = true;
//
//  appendMessage('USER', question, null, false);
//
//  // Add streaming placeholder
//  const assistantBubble = appendMessage('ASSISTANT', '…', null, true);
//  scrollToBottom();
//
//  try {
//    const resp = await apiFetch(`/api/chats/${currentChatId}/messages`, {
//      method: 'POST',
//      body: JSON.stringify({ question })
//    });
//    assistantBubble.classList.remove('streaming');
//    assistantBubble.textContent = resp.text;
//
//    // Append chunk toggle after the bubble's parent
//    if (resp.retrievedChunks && resp.retrievedChunks.length > 0) {
//      const right = assistantBubble.parentElement;
//      const toggle = document.createElement('button');
//      toggle.className = 'chunks-toggle';
//      toggle.textContent = `📚 ${resp.retrievedChunks.length} source chunk${resp.retrievedChunks.length > 1 ? 's' : ''} used`;
//      const chunksList = document.createElement('div');
//      chunksList.className = 'chunks-list hidden';
//      resp.retrievedChunks.forEach(c => {
//        const item = document.createElement('div');
//        item.className = 'chunk-item';
//        const src = document.createElement('div');
//        src.className = 'chunk-source';
//        src.textContent = [c.documentTitle, c.filePath, c.sourceType].filter(Boolean).join(' · ');
//        const txt = document.createElement('div');
//        txt.className = 'chunk-text';
//        txt.textContent = c.text;
//        item.appendChild(src);
//        item.appendChild(txt);
//        chunksList.appendChild(item);
//      });
//      toggle.addEventListener('click', () => chunksList.classList.toggle('hidden'));
//      right.appendChild(toggle);
//      right.appendChild(chunksList);
//    }
//  } catch (e) {
//    assistantBubble.classList.remove('streaming');
//    assistantBubble.textContent = '⚠ Error: ' + e.message;
//    assistantBubble.style.color = 'var(--danger)';
//  } finally {
//    btnSend.disabled = false;
//    scrollToBottom();
//  }
//}

//async function sendMessage() {
//  const question = inputText.value.trim();
//
//  if (!currentChatId) return;
//  if (!question && !pendingFile) return;
//
//  const fileToSend = pendingFile;
//
//  inputText.value = '';
//  inputText.style.height = 'auto';
//  btnSend.disabled = true;
//
//  appendMessage(
//    'USER',
//    question || '[File attached]',
//    null,
//    false,
//    fileToSend ? { fileName: fileToSend.name, fileUrl: null } : null
//  );
//
//  const assistantBubble = appendMessage('ASSISTANT', '…', null, true);
//  scrollToBottom();
//
//  try {
//    const formData = new FormData();
//
//    if (question) {
//      formData.append('question', question);
//    }
//
//    if (fileToSend) {
//      formData.append('file', fileToSend);
//    }
//
//    const resp = await apiFetch(`/api/chats/${currentChatId}/messages`, {
//      method: 'POST',
//      body: formData
//    });
//
//    assistantBubble.classList.remove('streaming');
//    assistantBubble.textContent = resp.text;
//
//    pendingFile = null;
//    renderPendingAttachment();
//  } catch (e) {
//    assistantBubble.classList.remove('streaming');
//    assistantBubble.textContent = '⚠ Error: ' + e.message;
//    assistantBubble.style.color = 'var(--danger)';
//  } finally {
//    btnSend.disabled = false;
//    scrollToBottom();
//  }
//}

async function sendMessage() {
  const question = inputText.value.trim();

  if (!currentChatId) return;
  if (!question && !pendingFile) return;

  const fileToSend = pendingFile;

  inputText.value = '';
  inputText.style.height = 'auto';
  btnSend.disabled = true;

  appendMessage(
    'USER',
    question || '[File attached]',
    null,
    false,
    fileToSend ? { fileName: fileToSend.name, fileUrl: null } : null
  );

  try {
    const formData = new FormData();

    if (question) {
      formData.append('question', question);
    }

    if (fileToSend) {
      formData.append('file', fileToSend);
    }

    const resp = await apiFetch(`/api/chats/${currentChatId}/messages`, {
      method: 'POST',
      body: formData
    });

    if (resp && resp.role && resp.text) {
      appendMessage(
        resp.role,
        resp.text,
        null,
        false,
        resp.fileName ? { fileName: resp.fileName, fileUrl: resp.fileUrl } : null
      );
    }

    pendingFile = null;
    renderPendingAttachment();
  } catch (e) {
    appendMessage('ASSISTANT', '⚠ Error: ' + e.message, null, false);
  } finally {
    btnSend.disabled = false;
    scrollToBottom();
  }
}

// ─── Sources ──────────────────────────────────────
async function loadSources() {
  if (!currentChatId) return;
  try {
    const sources = await apiFetch(`/api/chats/${currentChatId}/sources`);
    sourcesCount.textContent = sources.length;
    sourcesList.innerHTML = '';
    sources.forEach(s => {
      const chip = document.createElement('div');
      chip.className = `source-chip ${s.status.toLowerCase()}`;
      chip.textContent = `${sourceTypeIcon(s.type)} ${s.name}`;
      chip.title = `${s.type} · ${s.status} · ${s.indexKey || ''}`;
      sourcesList.appendChild(chip);
    });
  } catch (e) {
    console.warn('Failed to load sources', e);
  }
}

function sourceTypeIcon(type) {
  return { TEXT: '📝', PDF: '📄', GOOGLE_DOC: '🔗', CODEBASE: '💻' }[type] || '📎';
}

// ─── Model change ─────────────────────────────────
async function changeModel(chatId, modelKey) {
  if (modelKey === 'code-mode') {
    try {
      await apiFetch(`/api/chats/${chatId}/mode`, {
        method: 'PATCH',
        body: JSON.stringify({ mode: 'CODE_MODE' })
      });
      showToast('Switched to Code Mode', 'success');
    } catch (e) {
      showToast('Failed to switch mode: ' + e.message, 'error');
    }
    return;
  }
  try {
    await apiFetch(`/api/chats/${chatId}`, {
      method: 'PATCH',
      body: JSON.stringify({ modelKey })
    });
    showToast('Model updated', 'success');
  } catch (e) {
    showToast('Failed to update model: ' + e.message, 'error');
  }
}

// ─── Ingestion ────────────────────────────────────
async function uploadFile(chatId, file) {
  const formData = new FormData();
  formData.append('file', file);
  const endpoint = file.name.endsWith('.pdf')
    ? `/api/chats/${chatId}/sources/pdf`
    : `/api/chats/${chatId}/sources/text`;

  if (file.name.endsWith('.pdf')) {
    const res = await fetch(API + endpoint, { method: 'POST', body: formData });
    if (!res.ok) throw new Error(await res.text());
    return res.json();
  } else {
    // For text files: read and POST as JSON
    const text = await file.text();
    return apiFetch(endpoint, {
      method: 'POST',
      body: JSON.stringify({ name: file.name, text })
    });
  }
}

async function addText(chatId, name, text) {
  return apiFetch(`/api/chats/${chatId}/sources/text`, {
    method: 'POST',
    body: JSON.stringify({ name, text })
  });
}

async function addGoogleDoc(chatId, name, url) {
  return apiFetch(`/api/chats/${chatId}/sources/google-doc`, {
    method: 'POST',
    body: JSON.stringify({ name, url })
  });
}

// ─── Modals ───────────────────────────────────────
function openModal(modalId) {
  modalOverlay.classList.remove('hidden');
  document.querySelectorAll('.modal').forEach(m => m.classList.add('hidden'));
  $(modalId).classList.remove('hidden');
}
function closeModal() {
  modalOverlay.classList.add('hidden');
}

// ─── Event bindings ───────────────────────────────
function bindEvents() {
  // New chat button
  $('btn-new-chat').addEventListener('click', () => openModal('modal-new-chat'));
  $('btn-new-chat-cancel').addEventListener('click', closeModal);
  $('btn-new-chat-create').addEventListener('click', async () => {
    const title = $('new-chat-title').value.trim() || 'New Chat';
    const modelKey = $('new-chat-model').value;
    closeModal();
    await createChat(title, modelKey);
    $('new-chat-title').value = '';
  });

  // Model select change
//  modelSelect.addEventListener('change', () => {
//    if (currentChatId) changeModel(currentChatId, modelSelect.value);
//  });

  // Plus menu
  $('btn-plus').addEventListener('click', e => {
    e.stopPropagation();
    $('plus-menu').classList.toggle('hidden');
  });
  document.addEventListener('click', () => $('plus-menu').classList.add('hidden'));

  $('menu-upload-file').addEventListener('click', () => openModal('modal-file'));
  $('menu-google-doc').addEventListener('click', () => openModal('modal-google-doc'));
  $('menu-add-text').addEventListener('click', () => openModal('modal-text'));

  // File modal
  $('file-input').addEventListener('change', () => {
    const f = $('file-input').files[0];
    $('file-name-display').textContent = f ? f.name : '';
  });
  $('btn-file-cancel').addEventListener('click', closeModal);
$('btn-file-upload').addEventListener('click', () => {
  const f = $('file-input').files[0];
  if (!f) return;

  pendingFile = f;
  renderPendingAttachment();

  closeModal();
  showToast(`${f.name} attached`, 'success');

  $('file-input').value = '';
  $('file-name-display').textContent = '';
});

  // Google Doc modal
  $('btn-gdoc-cancel').addEventListener('click', closeModal);
  $('btn-gdoc-submit').addEventListener('click', async () => {
    const name = $('gdoc-name').value.trim() || 'Google Doc';
    const url = $('gdoc-url').value.trim();
    if (!url || !currentChatId) return;
    closeModal();
    showToast('Adding Google Doc…');
    try {
      await addGoogleDoc(currentChatId, name, url);
      showToast('Google Doc indexed', 'success');
      await loadSources();
    } catch (e) {
      showToast('Failed: ' + e.message, 'error');
    }
    $('gdoc-name').value = '';
    $('gdoc-url').value = '';
  });

  // Text modal
  $('btn-text-cancel').addEventListener('click', closeModal);
  $('btn-text-submit').addEventListener('click', async () => {
    const name = $('text-name').value.trim() || 'Text';
    const text = $('text-content').value.trim();
    if (!text || !currentChatId) return;
    closeModal();
    showToast('Indexing text…');
    try {
      await addText(currentChatId, name, text);
      showToast('Text indexed successfully', 'success');
      await loadSources();
    } catch (e) {
      showToast('Failed: ' + e.message, 'error');
    }
    $('text-name').value = '';
    $('text-content').value = '';
  });

  // Sources toggle
  $('btn-toggle-sources').addEventListener('click', () => {
    $('sources-list').classList.toggle('hidden');
  });

  // Send
  btnSend.addEventListener('click', sendMessage);
  inputText.addEventListener('keydown', e => {
    if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); sendMessage(); }
  });
  inputText.addEventListener('input', () => {
    inputText.style.height = 'auto';
    inputText.style.height = Math.min(inputText.scrollHeight, 140) + 'px';
  });

  // Close modal on overlay click
  modalOverlay.addEventListener('click', e => {
    if (e.target === modalOverlay) closeModal();
  });
}

// ─── Utilities ────────────────────────────────────
function scrollToBottom() {
  messagesArea.scrollTop = messagesArea.scrollHeight;
}

function createToastContainer() {
  if (!$('toast-container')) {
    const el = document.createElement('div');
    el.id = 'toast-container';
    document.body.appendChild(el);
  }
}

function showToast(msg, type = '') {
  const container = $('toast-container');
  const toast = document.createElement('div');
  toast.className = 'toast' + (type ? ' ' + type : '');
  toast.textContent = msg;
  container.appendChild(toast);
  setTimeout(() => toast.remove(), 3500);
}
