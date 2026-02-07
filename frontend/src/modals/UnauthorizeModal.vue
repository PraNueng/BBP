<template>
  <div
    v-if="visible"
    class="fixed inset-0 flex items-center justify-center backdrop-blur-sm bg-black/30 z-50"
  >
    <div
      class="bg-white rounded-2xl shadow-2xl w-11/12 sm:w-96 p-8 text-center relative"
    >
      <div class="flex justify-center mb-4">
        <div class="w-20 h-20 flex items-center justify-center bg-red-100 rounded-full">
          <svg
            class="w-10 h-10 text-red-500"
            fill="none"
            stroke="currentColor"
            stroke-width="3"
            viewBox="0 0 24 24"
          >
            <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </div>
      </div>

      <h2 class="text-2xl font-bold text-red-600 mb-2">
        ไม่มีสิทธิ์แก้ไขฟอร์มนี้
      </h2>

      <p class="text-gray-600 mb-6">
        คุณไม่สามารถแก้ไขฟอร์มของคนอื่นได้
      </p>

      <div class="flex justify-center">
        <button
          @click="handleClose"
          class="px-6 py-2 bg-gradient-to-r from-red-500 to-red-700 text-white font-semibold rounded-lg hover:from-red-600 hover:to-red-800 transition"
        >
          ปิด
        </button>
      </div>
      <button
        class="absolute top-1 right-5 text-gray-400 hover:text-gray-600"
        @click="handleClose"
      >
        ✕
      </button>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from "vue";
import { useRouter } from "vue-router";

const props = defineProps({
  visible: Boolean
});

const emit = defineEmits(["close"]);
const router = useRouter();

const handleClose = () => {
  emit("close");
  router.push("/list-hour-forms");
};
</script>

<style scoped>
.backdrop-blur-sm {
  backdrop-filter: blur(6px);
}
</style>
