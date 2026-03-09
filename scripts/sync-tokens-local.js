#!/usr/bin/env node
/**
 * Design Tokens 로컬 동기화 스크립트
 *
 * 사용법:
 *   node scripts/sync-tokens-local.js          → CSS → tokens + tokens → SCSS
 *   node scripts/sync-tokens-local.js --to-scss → tokens → SCSS만 실행
 *   node scripts/sync-tokens-local.js --to-json → CSS → tokens만 실행
 */

const fs = require('fs');
const path = require('path');

const ROOT = path.resolve(__dirname, '..');
const TOKENS_PATH = path.join(ROOT, 'design-tokens.json');
const CSS_PATH = path.join(ROOT, 'Coching-User-Vue/src/assets/css/style.css');
const SCSS_PATH = path.join(ROOT, 'Coching-User-Vue/src/assets/scss/style.scss');
const OUTPUT_SCSS = path.join(ROOT, 'Coching-User-Vue/src/assets/scss/_design-tokens.scss');

const args = process.argv.slice(2);
const toScssOnly = args.includes('--to-scss');
const toJsonOnly = args.includes('--to-json');

// ── CSS → design-tokens.json ──
function cssToTokens() {
  if (!fs.existsSync(CSS_PATH)) {
    console.log('⚠️ style.css 없음, 스킵');
    return;
  }

  const css = fs.readFileSync(CSS_PATH, 'utf8');
  const tokens = JSON.parse(fs.readFileSync(TOKENS_PATH, 'utf8'));

  // --color-- 변수 파싱
  const varMap = {};
  const re = /--color--([\w-]+)\s*:\s*([^;]+);/g;
  let m;
  while ((m = re.exec(css)) !== null) {
    varMap[m[1]] = m[2].trim();
  }

  const cochingKeyMap = {
    'primary': 'primary', 'wh': 'white', 'bk': 'black',
    'gray-666': 'gray666', 'gray-999': 'gray999', 'blue': 'blue',
    'border-ddd': 'borderDdd', 'border-eee': 'borderEee',
    'input-disabled': 'inputDisabled', 'bg-highlight': 'bgHighlight'
  };

  if (tokens.coching && tokens.coching.color) {
    let updated = 0;
    Object.entries(cochingKeyMap).forEach(([cssKey, tokenKey]) => {
      if (varMap[cssKey] && tokens.coching.color[tokenKey]) {
        if (tokens.coching.color[tokenKey].value !== varMap[cssKey]) {
          tokens.coching.color[tokenKey].value = varMap[cssKey];
          updated++;
        }
      }
    });
    console.log(`✅ CSS → Tokens: ${updated}개 COCHING 토큰 업데이트`);
  }

  // White Lab 토큰
  if (fs.existsSync(SCSS_PATH)) {
    const scss = fs.readFileSync(SCSS_PATH, 'utf8');
    const wlMap = {};
    const wlRe = /--wl-([\w-]+)\s*:\s*([^;]+);/g;
    while ((m = wlRe.exec(scss)) !== null) {
      wlMap[m[1]] = m[2].trim();
    }
    const globalKeyMap = {
      'bg':'bg','surface':'surface','sidebar':'sidebar','border':'border',
      'border-mid':'borderMid','accent':'accent','accent-lt':'accentLt',
      'accent-dim':'accentDim','text':'text','text-sub':'textSub','text-dim':'textDim',
      'green':'green','green-bg':'greenBg','red':'red','red-bg':'redBg',
      'amber':'amber','amber-bg':'amberBg','blue':'blue','blue-bg':'blueBg',
      'purple':'purple','purple-bg':'purpleBg'
    };
    if (tokens.global && tokens.global.color) {
      let updated = 0;
      Object.entries(globalKeyMap).forEach(([cssKey, tokenKey]) => {
        if (wlMap[cssKey] && tokens.global.color[tokenKey]) {
          if (tokens.global.color[tokenKey].value !== wlMap[cssKey]) {
            tokens.global.color[tokenKey].value = wlMap[cssKey];
            updated++;
          }
        }
      });
      console.log(`✅ SCSS → Tokens: ${updated}개 White Lab 토큰 업데이트`);
    }
  }

  fs.writeFileSync(TOKENS_PATH, JSON.stringify(tokens, null, 2) + '\n');
}

// ── design-tokens.json → SCSS 변수 ──
function tokensToScss() {
  const tokens = JSON.parse(fs.readFileSync(TOKENS_PATH, 'utf8'));

  let scss = '// ⚠️ 이 파일은 design-tokens.json에서 자동 생성됩니다.\n';
  scss += '// 직접 수정하지 마세요. design-tokens.json을 수정하세요.\n';
  scss += `// Generated: ${new Date().toISOString().split('T')[0]}\n\n`;

  // COCHING 토큰
  if (tokens.coching) {
    scss += '// ── COCHING Design Tokens ──\n';
    if (tokens.coching.color) {
      Object.entries(tokens.coching.color).forEach(([key, val]) => {
        const cssKey = key.replace(/([A-Z])/g, '-$1').toLowerCase();
        scss += `$coching-${cssKey}: ${val.value}; // ${val.description || ''}\n`;
      });
    }
    scss += '\n';
    if (tokens.coching.typography && tokens.coching.typography.fontSize) {
      scss += '// Typography\n';
      Object.entries(tokens.coching.typography.fontSize).forEach(([key, val]) => {
        scss += `$font-size-${key}: ${val.value}; // ${val.description || ''}\n`;
      });
    }
    scss += '\n';
    if (tokens.coching.spacing) {
      scss += '// Spacing\n';
      Object.entries(tokens.coching.spacing).forEach(([key, val]) => {
        const cssKey = key.replace(/([A-Z])/g, '-$1').toLowerCase();
        scss += `$spacing-${cssKey}: ${val.value}; // ${val.description || ''}\n`;
      });
    }
    scss += '\n';
    if (tokens.coching.borderRadius) {
      scss += '// Border Radius\n';
      Object.entries(tokens.coching.borderRadius).forEach(([key, val]) => {
        const cssKey = key.replace(/([A-Z])/g, '-$1').toLowerCase();
        scss += `$radius-${cssKey}: ${val.value}; // ${val.description || ''}\n`;
      });
    }
    scss += '\n';
    if (tokens.coching.components) {
      scss += '// Components\n';
      Object.entries(tokens.coching.components).forEach(([comp, props]) => {
        Object.entries(props).forEach(([key, val]) => {
          scss += `$${comp}-${key}: ${val.value}; // ${val.description || ''}\n`;
        });
      });
    }
  }

  scss += '\n// ── Global (White Lab) Tokens ──\n';
  if (tokens.global && tokens.global.color) {
    Object.entries(tokens.global.color).forEach(([key, val]) => {
      const cssKey = key.replace(/([A-Z])/g, '-$1').toLowerCase();
      scss += `$wl-${cssKey}: ${val.value}; // ${val.description || ''}\n`;
    });
  }
  if (tokens.global && tokens.global.borderRadius) {
    scss += `$wl-radius: ${tokens.global.borderRadius.base.value};\n`;
  }
  if (tokens.global && tokens.global.boxShadow) {
    scss += `$wl-shadow: ${tokens.global.boxShadow.base.value};\n`;
  }

  fs.writeFileSync(OUTPUT_SCSS, scss);
  console.log(`✅ Tokens → SCSS: ${OUTPUT_SCSS} 생성 완료`);
}

// 실행
console.log('🔄 Design Tokens 동기화 시작...\n');
if (!toScssOnly) cssToTokens();
if (!toJsonOnly) tokensToScss();
console.log('\n✨ 완료!');
