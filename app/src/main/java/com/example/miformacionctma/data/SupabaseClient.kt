package com.example.miformacionctma.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseConfig {
    const val URL = "https://cofvvrtqfsmjttgrvvkw.supabase.co"
    const val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNvZnZ2cnRxZnNtanR0Z3J2dmt3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODg5NDc0NTEsImV4cCI6MjEwNDUyMzQ1MX0.G6dF8HPfekvQwhfLm1woNCifEfm9PgG_KWd6ZD-3HT8"
}

val supabaseClient: SupabaseClient = createSupabaseClient(
    supabaseUrl = SupabaseConfig.URL,
    supabaseKey = SupabaseConfig.API_KEY
) {
    install(Postgrest)
}
