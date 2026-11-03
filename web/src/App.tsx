const statusItems = [
  { label: 'Код', value: 'Открыт с первого дня' },
  { label: 'Первая платформа', value: 'Forge 1.20.1' },
  { label: 'Публичный запуск', value: 'После security и legal review' },
] as const

const productRows = [
  {
    title: 'Мир остаётся у владельца',
    text: 'Keep Together проводит друзей к IntegratedServer через зашифрованный relay и не загружает сохранение в облако.',
  },
  {
    title: 'Идентичность не зависит от ника',
    text: 'Друзья, блокировки и права мира привязаны к постоянному Keep Together ID, а не к изменяемому Minecraft-имени.',
  },
  {
    title: 'Сообщения проектируются как E2E',
    text: 'Backend доставляет шифротекст и не получает мастер-ключ. Функция не выйдет публично до правовой и независимой проверки.',
  },
] as const

const worldDiagram = (
  <div className="world-diagram" aria-label="Два мира соединены защищённым каналом">
    <div className="world-block world-block--owner">
      <span className="world-block__label">Мир друга</span>
      <span className="world-block__state">открыт</span>
    </div>
    <div className="world-link" aria-hidden="true">
      <span className="world-link__node" />
      <span className="world-link__line" />
      <span className="world-link__node" />
    </div>
    <div className="world-block world-block--guest">
      <span className="world-block__label">Ваш клиент</span>
      <span className="world-block__state">подключён</span>
    </div>
  </div>
)

export function App() {
  return (
    <div className="site-shell">
      <header className="site-header">
        <a className="brand" href="#top" aria-label="Keep Together — наверх">
          <span className="brand__mark" aria-hidden="true">KT</span>
          <span>Keep Together</span>
        </a>
        <span className="release-state">
          <span className="release-state__dot" aria-hidden="true" />
          Pre-alpha
        </span>
      </header>

      <main id="top">
        <section className="hero" aria-labelledby="hero-title">
          <div className="hero__copy">
            <h1 id="hero-title">Открывайте свой мир только тем, кого знаете.</h1>
            <p>
              Keep Together — открытый Forge-мод с собственной системой друзей,
              профилей, скинов и временных миров на компьютере владельца.
            </p>
            <div className="hero__actions">
              <a className="primary-action" href="#status">Проверить статус</a>
              <a className="text-action" href="#scope">Что войдёт в мод</a>
            </div>
          </div>
          {worldDiagram}
        </section>

        <section className="status-ledger" id="status" aria-label="Статус проекта">
          {statusItems.map((item) => (
            <div className="status-ledger__item" key={item.label}>
              <span>{item.label}</span>
              <strong>{item.value}</strong>
            </div>
          ))}
        </section>

        <section className="scope" id="scope" aria-labelledby="scope-title">
          <div className="scope__heading">
            <h2 id="scope-title">Один мод. Три понятных обещания.</h2>
            <p>
              Никакого launcher, публичного каталога миров или скрытой установки
              стороннего кода.
            </p>
          </div>
          <div className="scope__rows">
            {productRows.map((row) => (
              <article className="scope-row" key={row.title}>
                <h3>{row.title}</h3>
                <p>{row.text}</p>
              </article>
            ))}
          </div>
        </section>

        <section className="notice" aria-labelledby="notice-title">
          <h2 id="notice-title">Сейчас идёт техническая подготовка.</h2>
          <p>
            Готовой сборки ещё нет. До public beta проект должен пройти
            нагрузочные тесты, независимую проверку безопасности и российскую
            юридическую проверку.
          </p>
        </section>
      </main>

      <footer className="site-footer">
        <p>
          Keep Together не является официальным продуктом Minecraft и не связан
          с Mojang или Microsoft.
        </p>
        <p>MIT License · 2026</p>
      </footer>
    </div>
  )
}
