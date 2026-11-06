import re

with open('src/main/resources/templates/index.html', 'r', encoding='utf-8') as f:
    text = f.read()

# 1. Background and selection
text = text.replace('selection:bg-purple-600 selection:text-white', 'selection:bg-neutral-200 selection:text-neutral-900')
text = text.replace('bg-purple-700/25', 'bg-white/[0.02]')
text = text.replace('bg-fuchsia-600/20', 'bg-white/[0.02]')
text = text.replace('bg-violet-600/25', 'bg-white/[0.02]')
text = text.replace('bg-purple-700/20', 'bg-white/[0.02]')
text = text.replace('bg-fuchsia-600/15', 'bg-white/[0.02]')

# 2. Glowing purple halos behind cards
text = text.replace('bg-gradient-to-r from-violet-600 via-purple-600 to-fuchsia-600 opacity-35 blur-xl group-hover:opacity-50', 'hidden')
text = text.replace('bg-gradient-to-r from-violet-600 via-purple-600 to-fuchsia-600', 'bg-neutral-100 text-neutral-950 hover:bg-white')
text = text.replace('shadow-[0_0_24px_rgba(168,85,247,0.45)] hover:shadow-[0_0_32px_rgba(217,70,239,0.65)]', 'shadow-lg hover:shadow-xl')
text = text.replace('shadow-[0_0_28px_rgba(168,85,247,0.45)] hover:shadow-[0_0_38px_rgba(217,70,239,0.65)]', 'shadow-lg hover:shadow-xl')

# 3. Text gradients (e.g. "Land the role.")
text = text.replace(
    'bg-gradient-to-r from-violet-400 via-fuchsia-400 to-pink-400 bg-clip-text text-transparent',
    'bg-gradient-to-r from-white via-neutral-200 to-neutral-400 bg-clip-text text-transparent'
)

# 4. Borders and container colors
text = re.sub(r'border-purple-\d+/\d+', 'border-neutral-800', text)
text = re.sub(r'border-fuchsia-\d+/\d+', 'border-neutral-800', text)
text = re.sub(r'border-violet-\d+/\d+', 'border-neutral-800', text)

# 5. Badges, chips, and backgrounds
text = re.sub(r'bg-purple-\d+/\d+', 'bg-neutral-900 border border-neutral-700', text)
text = re.sub(r'bg-fuchsia-\d+/\d+', 'bg-neutral-900 border border-neutral-700', text)
text = re.sub(r'bg-violet-\d+/\d+', 'bg-neutral-900 border border-neutral-700', text)
text = re.sub(r'text-purple-\d+', 'text-neutral-300', text)
text = re.sub(r'text-fuchsia-\d+', 'text-neutral-300', text)
text = re.sub(r'text-violet-\d+', 'text-neutral-300', text)
text = re.sub(r'text-emerald-\d+', 'text-neutral-300', text)
text = re.sub(r'bg-emerald-\d+/\d+', 'bg-neutral-900 border border-neutral-700 text-neutral-200', text)
text = re.sub(r'bg-emerald-\d+', 'bg-neutral-200', text)

# 6. SVG LinearGradients
text = re.sub(
    r'<linearGradient id="score-gradient"[^>]*>.*?</linearGradient>',
    '''<linearGradient id="score-gradient" x1="0%" x2="100%" y1="0%" y2="100%">
<stop offset="0%" stop-color="#ffffff"></stop>
<stop offset="50%" stop-color="#d4d4d8"></stop>
<stop offset="100%" stop-color="#71717a"></stop>
</linearGradient>''',
    text,
    flags=re.DOTALL
)

# 7. Progress bar in Alex Rivera card
text = re.sub(r'bg-gradient-to-r from-violet-500 via-fuchsia-500 to-pink-500', 'bg-neutral-200', text)
text = re.sub(r'shadow-\[0_0_12px_rgba\(217,70,239,0\.5\)\]', 'shadow', text)

# 8. Check marks & icons
text = text.replace('text-emerald-400', 'text-neutral-300')
text = text.replace('text-fuchsia-400', 'text-neutral-300')
text = text.replace('text-violet-400', 'text-neutral-300')

with open('src/main/resources/templates/index.html', 'w', encoding='utf-8') as f:
    f.write(text)

print("index.html converted to pure grayscale")

