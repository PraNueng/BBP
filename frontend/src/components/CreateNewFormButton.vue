<template>
  <button
    @click="goToHourForms"
    class="flex items-center justify-center flex-nowrap
           bg-gradient-to-r from-gray-500 to-gray-700 hover:bg-gray-900 text-white font-semibold
           shadow-md transition-all duration-200
           px-4 py-2 rounded-full
           md:px-6 md:py-3"
  >
    <span class="tracking-wide whitespace-nowrap">+ บันทึกการสอน</span>
  </button>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goToHourForms = () => {
  router.push('/hour-forms')
}
</script>

<script setup></script>
