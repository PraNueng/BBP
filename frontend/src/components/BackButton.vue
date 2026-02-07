<template>
  <div class="flex justify-start px-6 py-4">
    <button
      @click="handleBack"
      class="flex items-center space-x-2 
             px-4 py-2 md:px-5 md:py-2.5
             bg-gradient-to-r from-red-400 to-red-600 
             text-white font-semibold rounded-full 
             shadow-md hover:shadow-xl hover:scale-105
             active:scale-95 transition-all duration-300 ease-out"
    >
      <svg xmlns="http://www.w3.org/2000/svg"
        class="w-5 h-5 md:w-6 md:h-6"
        fill="none"
        viewBox="0 0 24 24"
        stroke="currentColor"
        stroke-width="3">
        <path stroke-linecap="round" stroke-linejoin="round" d="M15 19l-7-7 7-7" />
      </svg>
      <span class="hidden sm:inline text-base md:text-lg md:w-20">
        ย้อนกลับ
      </span>
    </button>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const props = defineProps({
  to: {
    type: String,
    default: null
  }
})

const handleBack = () => {
  if (props.to) {
    router.push(props.to)
  } else {
    router.back()
  }
}
</script>