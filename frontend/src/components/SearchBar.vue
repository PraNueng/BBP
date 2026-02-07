<script setup>
import { computed } from 'vue'
import ClearButton from '@/components/ClearButton.vue'

const props = defineProps({
  modelValue: String,
  placeholder: {
    type: String,
    default: 'ค้นหาชื่อน้องหรือเนื้อหา'
  }
})

const emit = defineEmits(['update:modelValue', 'search'])

const inputValue = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const clearInput = () => {
  emit('update:modelValue', '')
  emit('search') // ให้รีเฟรชผลลัพธ์หลังเคลียร์
}
</script>

<template>
  <div class="relative w-full">
    <input
      v-model="inputValue"
      @keyup.enter="$emit('search')"
      type="text"
      :placeholder="placeholder"
      class="pl-14 pr-12 py-3 border-2 border-gray-300 rounded-full w-full 
             focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200 
             transition-all shadow-sm hover:shadow-md placeholder:text-sm"
    />

    <!-- ปุ่มค้นหา -->
    <button
      @click="$emit('search')"
      class="absolute left-0 top-1/2 -translate-y-1/2 p-3 bg-blue-500 
             rounded-full shadow-md hover:bg-blue-600 transition-all"
    >
      <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
      </svg>
    </button>

    <!-- ปุ่มเคลียร์ -->
    <ClearButton :visible="!!inputValue" @clear="clearInput" class="pr-2" />
  </div>
</template>
