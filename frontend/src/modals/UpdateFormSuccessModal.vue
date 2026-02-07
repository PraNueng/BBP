<template>
  <div
    v-if="visible"
    class="fixed inset-0 flex items-center justify-center bg-black/40 backdrop-blur-sm z-50"
  >
    <div class="bg-white w-11/12 sm:w-96 p-8 rounded-2xl shadow-2xl text-center relative">
      <div class="flex justify-center mb-4">
        <div class="w-20 h-20 bg-blue-100 rounded-full flex items-center justify-center">
          <svg
            class="w-10 h-10 text-blue-600"
            fill="none"
            stroke="currentColor"
            stroke-width="3"
            viewBox="0 0 24 24"
          >
            <path stroke-linecap="round" stroke-linejoin="round" d="M5 13l4 4L19 7" />
          </svg>
        </div>
      </div>

      <h2 class="text-2xl font-bold text-blue-600 mb-3">แก้ไขสำเร็จ!</h2>
      <p class="text-gray-600 mb-6">ข้อมูลถูกแก้ไขเรียบร้อยแล้ว</p>

      <button
        @click="confirm"
        class="px-8 py-2 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-lg transition"
      >
        ตกลง
      </button>

      <button @click="confirm" class="absolute top-1 right-5 text-gray-500 text-xl">
        ✕
      </button>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from "vue";

const props = defineProps({
  visible: Boolean,
});
const emit = defineEmits(["confirm"]);

const confirm = () => emit("confirm");
</script>
