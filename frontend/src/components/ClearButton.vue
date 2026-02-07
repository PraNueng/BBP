<template>
  <button
    v-if="visible"
    @click="$emit('clear')"
    class="absolute right-3 top-1/2 -translate-y-1/2 
           text-gray-400 hover:text-red-500 transition"
    aria-label="Clear search"
  >
    <svg xmlns="http://www.w3.org/2000/svg" 
         class="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
      <path fill-rule="evenodd" 
            d="M10 8.586l4.95-4.95a1 1 0 111.414 1.414L11.414 10l4.95 4.95a1 1 0 11-1.414 1.414L10 11.414l-4.95 4.95a1 1 0 11-1.414-1.414L8.586 10l-4.95-4.95A1 1 0 115.05 3.636L10 8.586z"
            clip-rule="evenodd" />
    </svg>
  </button>
</template>

<script setup>
defineProps({
  visible: {
    type: Boolean,
    default: false
  }
})

defineEmits(['clear'])


const clearInput = () => {
  emit('update:modelValue', '')
  emit('search')
}
</script>
