(function () {
  const toast = document.getElementById('toast');

  function showToast(msg) {
    if (!toast) return;
    toast.textContent = msg || '';
    toast.classList.add('show');
    setTimeout(() => toast.classList.remove('show'), 1200);
  }

  async function toggleKR(krItem) {
    const krId = krItem.getAttribute('data-kr-id');
    const btn = krItem.querySelector('.toggle-btn');

    // 낙관적 UI
    const wasChecked = krItem.classList.contains('is-checked');
    krItem.classList.toggle('is-checked', !wasChecked);
    btn.classList.toggle('is-on', !wasChecked);
    btn.setAttribute('aria-pressed', String(!wasChecked));
    btn.querySelector('span').textContent = !wasChecked ? '완료' : '체크';

    try {
      const res = await fetch(`/api/objective/kr/${krId}/toggle`, { // [CHANGED] 경로
        method: 'POST'
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data = await res.json();

      // 서버 결과로 동기화
      krItem.classList.toggle('is-checked', data.checked);
      btn.classList.toggle('is-on', data.checked);
      btn.setAttribute('aria-pressed', String(data.checked));
      btn.querySelector('span').textContent = data.checked ? '완료' : '체크';

      // 통계 갱신
      const meta = krItem.querySelector('.kr-meta');
      if (meta) {
        const week = (typeof data.weekCount === 'number') ? data.weekCount : '-';
        const month = (typeof data.monthCount === 'number') ? data.monthCount : '-';
        const streak = (typeof data.streak === 'number') ? data.streak : '-';
        meta.innerHTML = `주간 <span>${week}</span> · 월간 <span>${month}</span> · 연속 <span>${streak}</span>일`;
      }

      if (data.toast) showToast(data.toast);
    } catch (e) {
      // 실패 롤백
      krItem.classList.toggle('is-checked', wasChecked);
      btn.classList.toggle('is-on', wasChecked);
      btn.setAttribute('aria-pressed', String(wasChecked));
      btn.querySelector('span').textContent = wasChecked ? '완료' : '체크';
      showToast('잠시 후 다시 시도해주세요.');
      console.error(e);
    }
  }

  // 이벤트 위임
  document.addEventListener('click', (e) => {
    const btn = e.target.closest('.toggle-btn');
    if (!btn) return;
    const item = btn.closest('.kr-item');
    if (item) toggleKR(item);
  });
})();