<template>
  <button
    @click="$emit('edit')"
    class="px-4 py-2 bg-gradient-to-r from-yellow-400 to-orange-500 text-white rounded-lg scale-80
           hover:from-yellow-500 hover:to-orange-600 text-sm font-semibold shadow-md 
           hover:shadow-lg transition-all transform hover:scale-105 flex items-center gap-1"
  >
    <!-- <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path
        stroke-linecap="round"
        stroke-linejoin="round"
        stroke-width="2"
        d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0
        002-2v-5m-1.414-9.414a2 2 0
        112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"
      />
    </svg> -->
    <span>ข้อมูลส่วนตัว</span>
  </button>
</template>

<script setup>
</script>
