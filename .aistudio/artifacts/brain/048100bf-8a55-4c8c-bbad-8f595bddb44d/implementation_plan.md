# Perfeccionamiento de El Impostor: Pistas Naturales LATAM, Multijugador con 1–3 Impostores y Corrección de Transición

Este plan detalla los ajustes para que **El Impostor** tenga pistas naturales y creíbles en contexto latinoamericano, soporte partidas con **1, 2 o 3 impostores**, aplique la regla de victoria cuando los impostores igualan en número a los inocentes, y elimine por completo el destello de la nueva palabra al cambiar de ronda.

---

## Decisiones Confirmadas por el Usuario

> [!IMPORTANT]
> Estas decisiones fueron confirmadas directamente por el usuario:

- **Estilo de Pistas (Contexto Cotidiano LATAM)**: Cada pista será **1 palabra (o 2 como máximo)** totalmente natural y relacionada con la palabra secreta (algo que cualquier persona diría sin levantar sospechas, como *"Ropa"* para *Maleta*, *"Millonario"* para *El Juego del Calamar*, *"Lluvia"* para *Paraguas* o *"Salsa"* para *Tacos*), sin ser rebuscada ni regalar la respuesta exacta.
- **Cantidad de Impostores (1, 2 o 3)**:
  - **1 Impostor**: Disponible desde **3 jugadores**.
  - **2 Impostores**: Disponible desde **5 jugadores**.
  - **3 Impostores**: Disponible desde **7 jugadores**.
- **Condición de Victoria del Impostor**: Los impostores ganan únicamente cuando queda **la misma cantidad de impostores vivos que de inocentes vivos** (`impostores == inocentes`), mientras que los inocentes ganan al descubrir y expulsar a todos los impostores.

---

## 1. Resumen de Mejoras

1. **Renovación Total del Banco de Pistas (7 Categorías)**:
   - Se eliminan las palabras filosóficas o rebuscadas (*"Paradoja"*, *"Escapada"*, *"Consecuencia"*, *"Infancia"*) y se reemplazan por asociaciones cotidianas que sirven como salvavidas real en una conversación entre amigos en Latinoamérica.
2. **Selector de 1, 2 o 3 Impostores con Validación Automática**:
   - Selector interactivo en la pantalla de preparación que habilita **2 impostores** al tener 5+ jugadores y **3 impostores** al tener 7+ jugadores.
   - Si el grupo reduce la lista de jugadores por debajo del mínimo requerido, la cantidad de impostores se ajusta automáticamente.
3. **Flujo de Votación y Expulsión para Múltiples Impostores**:
   - Al expulsar a un jugador cuando se juega con 2 o 3 impostores, si atrapan a un impostor pero aún queda otro oculto, se revela que **sí era Impostor** y la partida continúa hasta atrapar a los demás o hasta que los impostores igualen en número a los inocentes.
4. **Eliminación del Destello (Flash) de la Nueva Palabra al Reiniciar Ronda**:
   - La pantalla de fin de partida (`GAME_OVER`) mostrará una copia congelada (`RoundOverSnapshot`) de la ronda finalizada. Así, al presionar *"Jugar Otra Ronda Rápida"*, la transición de salida nunca mostrará la palabra secreta recién sorteada para la siguiente partida.

---

## 2. Arquitectura y Flujo de Estado

```
┌──────────────────────────────────────────────────────────────────────────┐
│                     CONFIGURACIÓN (ImpostorPhase.SETUP)                  │
│  • Jugadores Persistentes (Mín. 3)                                       │
│  • Selector de Impostores:                                               │
│    - [1 Impostor] (3+ jug.)                                              │
│    - [2 Impostores] (5+ jug.)                                            │
│    - [3 Impostores] (7+ jug.)                                            │
└────────────────────────────────────┬─────────────────────────────────────┘
                                     │
                                     ▼
┌──────────────────────────────────────────────────────────────────────────┐
│               SORTEO Y REVELACIÓN (PASS_AND_REVEAL -> DEBATE)            │
│  • Pistas de 1-2 palabras naturales LATAM (ej. "Ropa", "Millonario")     │
│  • Categoría oculta si el modo es "Todo Mezclado"                        │
│  • Probabilidad baja (~12%) de que un Impostor inicie la ronda           │
└────────────────────────────────────┬─────────────────────────────────────┘
                                     │
                                     ▼
┌──────────────────────────────────────────────────────────────────────────┐
│                     VOTACIÓN MANUAL Y CONDICIÓN DE FIN                   │
│  • ¿Impostores vivos == 0? ───────────► ¡Ganan los Inocentes!            │
│  • ¿Impostores vivos == Inocentes? ───► ¡Ganan los Impostores!           │
│  • De lo contrario ───────────────────► Revelar rol del expulsado y      │
│                                         continuar debate                 │
├──────────────────────────────────────────────────────────────────────────┤
│  • RoundOverSnapshot inmutable: evita que la nueva palabra parpadee      │
│    durante la animación de salida al iniciar otra ronda rápida.          │
└──────────────────────────────────────────────────────────────────────────┘
```

### Auditoría y Verificación
- Al finalizar la implementación se ejecutará `compile_applet` y `gradle :app:testDebugUnitTest` para verificar que no existan errores ni incompatibilidades.
