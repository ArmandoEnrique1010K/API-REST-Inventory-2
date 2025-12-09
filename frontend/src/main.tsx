import React from 'react'
import ReactDOM from 'react-dom/client'
import { Router } from './router/Router'
import './styles.css'
import { ReactNotifications } from 'react-notifications-component'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'

const queryClient = new QueryClient()

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <QueryClientProvider client={queryClient}>
      <ReactNotifications />
      <Router />
    </QueryClientProvider>
  </React.StrictMode>,
)
