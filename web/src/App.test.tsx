import { renderToStaticMarkup } from 'react-dom/server'
import { describe, expect, it } from 'vitest'
import { App } from './App'

describe('App', () => {
  it('renders the honest pre-alpha product status', () => {
    const markup = renderToStaticMarkup(<App />)

    expect(markup).toContain('Keep Together')
    expect(markup).toContain('Pre-alpha')
    expect(markup).toContain('Готовой сборки ещё нет')
  })
})
