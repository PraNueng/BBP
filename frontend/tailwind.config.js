/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      screens: {
        'xs': '375px',
      },
    height: {
      'screen-dvh': '100dvh',
    },
    minHeight: {
      'screen-dvh': '100dvh',
    },
    },
  },
}