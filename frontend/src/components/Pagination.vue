<template>
  <div class="flex justify-center mt-8 mb-4 scale-90 md:scale-100">
    <div class="bg-white rounded-full border-2 border-gray-200 px-3 md:px-6 py-2 md:py-3 flex items-center gap-1 md:gap-3">
      <button
        @click="goTo(1)"
        :disabled="currentPageInternal === 1"
        class="flex items-center justify-center
              w-15 h-15 px-2 md:px-4 py-1 md:py-2 text-xs md:text-base rounded-full
              border-2 border-gray-300 transition-all font-semibold
              hover:bg-blue-500 hover:text-white hover:border-blue-500
              disabled:opacity-40 disabled:cursor-not-allowed
              disabled:hover:bg-white disabled:hover:text-gray-400"
      >
        First
      </button>

      <button
        @click="prev"
        :disabled="currentPageInternal === 1"
        class="w-7 h-7 md:w-10 md:h-10 flex items-center justify-center rounded-full border-2 border-gray-300 hover:bg-blue-500 hover:text-white hover:border-blue-500 transition-all font-bold text-sm md:text-base disabled:opacity-40 disabled:cursor-not-allowed disabled:hover:bg-white disabled:hover:text-gray-400"
      >
        &lt;
      </button>

      <div class="bg-gradient-to-r from-blue-50 to-indigo-50 rounded-full px-2 md:px-4 py-1 md:py-2 flex items-center gap-1 md:gap-2 border-2 border-blue-200">
        <button
          v-for="page in visiblePages"
          :key="page"
          @click="goTo(page)"
          :class="[
            'w-12 h-7 flex items-center justify-center rounded-full border-2 transition-all font-semibold text-xs md:text-base',
            currentPageInternal === page
              ? 'bg-gradient-to-r from-blue-500 to-indigo-600 text-white border-blue-600 shadow-lg scale-110'
              : 'bg-white border-gray-300 hover:bg-blue-100 hover:border-blue-400 hover:scale-105'
          ]"
        >
          {{ page }}
        </button>
      </div>

      <button
        @click="next"
        :disabled="currentPageInternal === totalPages"
        class="w-7 h-7 md:w-10 md:h-10 flex items-center justify-center rounded-full border-2 border-gray-300 hover:bg-blue-500 hover:text-white hover:border-blue-500 transition-all font-bold text-sm md:text-base disabled:opacity-40 disabled:cursor-not-allowed disabled:hover:bg-white disabled:hover:text-gray-400"
      >
        &gt;
      </button>

      <button
        @click="goTo(totalPages)"
        :disabled="currentPageInternal === totalPages"
        class="flex items-center justify-center
              w-15 h-15 px-2 md:px-4 py-1 md:py-2 text-xs md:text-base rounded-full
              border-2 border-gray-300 transition-all font-semibold
              hover:bg-blue-500 hover:text-white hover:border-blue-500
              disabled:opacity-40 disabled:cursor-not-allowed
              disabled:hover:bg-white disabled:hover:text-gray-400"
      >
        Last
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'

const props = defineProps({
  modelValue: { type: Number, default: 1 },
  totalPages: { type: Number, required: true }
})

const emit = defineEmits(['update:modelValue'])
const currentPageInternal = ref(props.modelValue)

watch(() => props.modelValue, val => {
  currentPageInternal.value = val
})

const visiblePages = computed(() => {
  const total = props.totalPages
  const current = currentPageInternal.value
  const maxVisible = 3
  
  if (total <= maxVisible) {
    return Array.from({ length: total }, (_, i) => i + 1)
  }
  
  const half = Math.floor(maxVisible / 2)
  let start = Math.max(1, current - half)
  let end = Math.min(total, start + maxVisible - 1)
  
  if (end - start < maxVisible - 1) {
    start = Math.max(1, end - maxVisible + 1)
  }
  
  return Array.from({ length: end - start + 1 }, (_, i) => start + i)
})

const goTo = (page) => {
  currentPageInternal.value = page
  emit('update:modelValue', page)
}

const prev = () => {
  if(currentPageInternal.value > 1){
    goTo(currentPageInternal.value - 1)
  }
}

const next = () => {
  if(currentPageInternal.value < props.totalPages){
    goTo(currentPageInternal.value + 1)
  }
}
</script>