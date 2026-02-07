<template>
  <div class="text-center py-12">
    <svg
      xmlns="http://www.w3.org/2000/svg"
      class="h-24 w-24 text-gray-400 mx-auto mb-4"
      fill="none"
      viewBox="0 0 24 24"
      stroke="currentColor"
    >
      <path
        stroke-linecap="round"
        stroke-linejoin="round"
        stroke-width="2"
        d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
      />
    </svg>
    <p class="text-xl text-gray-600 font-medium">{{ message }}</p>
  </div>
</template>

<script setup>
defineProps({
  message: {
    type: String,
    default: 'ไม่มีข้อมูลตรงกับเงื่อนไข',
  },
})
</script>
