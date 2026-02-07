<template>
  <div class="fixed inset-0 backdrop-blur-sm bg-black/60 flex items-center justify-center z-50 p-4" @click.self="$emit('close')">
    <div :class="['rounded-3xl shadow-2xl w-full overflow-hidden relative', wrapperClass]">
      <!-- background accents (kept minimal and optional) -->
      <div v-if="showBackground" class="absolute inset-0 overflow-hidden pointer-events-none">
        <div class="absolute w-64 h-64 bg-white/5 rounded-full blur-3xl -top-32 -left-32 opacity-30"></div>
        <div class="absolute w-64 h-64 bg-white/5 rounded-full blur-3xl -bottom-32 -right-32 opacity-30"></div>
      </div>

      <!-- header -->
      <div :class="['relative z-10 p-6', headerClass]">
        <div class="flex items-center justify-between">
          <div class="flex items-center gap-3">
            <slot name="icon" />
            <h2 class="text-2xl font-bold text-white"><slot name="title"></slot></h2>
          </div>
          <button @click="$emit('close')" class="text-white hover:bg-white/20 rounded-full p-2 transition group">
            <slot name="closeIcon">✕</slot>
          </button>
        </div>
      </div>

      <!-- body -->
      <div class="relative z-10 overflow-y-auto" :style="bodyStyle">
        <div class="p-6">
          <slot />
        </div>
      </div>

      <!-- footer slot if needed -->
      <slot name="footer" />
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  wrapperClass: { type: String, default: 'bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900 max-w-2xl max-h-[90vh]' },
  headerClass: { type: String, default: 'bg-gradient-to-r from-green-600 to-emerald-700 rounded-t-3xl shadow-lg' },
  bodyStyle: { type: Object, default: () => ({ maxHeight: 'calc(90vh - 180px)' }) },
  showBackground: { type: Boolean, default: true }
});
</script>

<style scoped>
/* minimal styles — layout is managed by parent */
</style>
