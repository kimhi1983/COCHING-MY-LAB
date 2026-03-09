/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      fontFamily: {
        display: ['var(--font-display)'],
        mono: ['var(--font-mono)'],
        body: ['var(--font-body)'],
      },
      colors: {
        lab: {
          bg: '#0A0C10',
          surface: '#111318',
          card: '#161B24',
          border: '#1E2530',
          accent: '#00E5FF',
          accent2: '#7B68EE',
          success: '#00FF88',
          warn: '#FFB800',
          danger: '#FF4444',
          muted: '#4A5568',
          text: '#E2E8F0',
          dim: '#718096',
        },
      },
      backgroundImage: {
        'grid-lab': "linear-gradient(rgba(0,229,255,0.03) 1px, transparent 1px), linear-gradient(90deg, rgba(0,229,255,0.03) 1px, transparent 1px)",
        'glow-accent': 'radial-gradient(circle at 50% 50%, rgba(0,229,255,0.15) 0%, transparent 70%)',
      },
      backgroundSize: {
        'grid': '40px 40px',
      },
      animation: {
        'pulse-slow': 'pulse 3s cubic-bezier(0.4, 0, 0.6, 1) infinite',
        'scan': 'scan 2s linear infinite',
        'float': 'float 6s ease-in-out infinite',
      },
      keyframes: {
        scan: {
          '0%': { transform: 'translateY(-100%)' },
          '100%': { transform: 'translateY(100vh)' },
        },
        float: {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-8px)' },
        }
      },
      boxShadow: {
        'glow': '0 0 20px rgba(0,229,255,0.3)',
        'glow-sm': '0 0 10px rgba(0,229,255,0.2)',
        'glow-success': '0 0 20px rgba(0,255,136,0.3)',
        'glow-warn': '0 0 20px rgba(255,184,0,0.3)',
      }
    },
  },
  plugins: [],
}
