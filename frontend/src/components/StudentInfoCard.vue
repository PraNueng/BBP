<template>
  <div class="bg-slate-700/30 backdrop-blur-sm rounded-xl p-4 border border-slate-600/50 hover:border-blue-500/50 hover:shadow-lg transition-all">
    <div class="flex items-start gap-3">
      <div class="p-2 bg-blue-500/20 rounded-lg">
        <component :is="iconComponent" class="w-5 h-5 text-blue-400" />
      </div>
      <div class="flex-1">
        <p class="text-xs text-gray-400 font-medium mb-1">{{ label }}</p>
        <p class="text-white font-semibold break-words">{{ value }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, h } from 'vue';

const props = defineProps({
  icon: String,
  label: String,
  value: String
});

const iconComponent = computed(() => {
  const icons = {
    school: 'M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4',
    book: 'M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253',
    phone: 'M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z',
    code: 'M7 20l4-16m2 16l4-16M6 9h14M4 15h14',
    calendar: 'M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z'
  };

  return h('svg', {
    fill: 'none',
    stroke: 'currentColor',
    viewBox: '0 0 24 24',
  }, [
    h('path', {
      'stroke-linecap': 'round',
      'stroke-linejoin': 'round',
      'stroke-width': '2',
      d: icons[props.icon]
    })
  ]);
});
</script>