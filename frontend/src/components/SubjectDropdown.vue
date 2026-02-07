<template>
  <div class="relative w-full">
    <label class="block text-gray-700 font-semibold mb-2">
      วิชาที่สอน <span class="text-red-500">*</span>
    </label>
    
    <button
      type="button"
      @click="showDropdown = !showDropdown"
      class="w-full px-4 py-3 bg-white border-2 rounded-lg font-semibold transition-all flex items-center justify-between"
      :class="[
        error ? 'border-red-500' : 'border-gray-300 hover:border-yellow-400',
        modelValue ? 'text-gray-800' : 'text-gray-400'
      ]"
    >
      <span class="flex items-center gap-2">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" 
                d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
        </svg>
        {{ modelValue || 'เลือกวิชาที่สอน' }}
      </span>
      <svg 
        xmlns="http://www.w3.org/2000/svg" 
        class="h-5 w-5 transition-transform"
        :class="{ 'rotate-180': showDropdown }"
        fill="none" 
        viewBox="0 0 24 24" 
        stroke="currentColor"
      >
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
      </svg>
    </button>

    <p v-if="error" class="text-red-500 text-sm mt-1">{{ error }}</p>

    <div
      v-if="showDropdown"
      class="absolute top-full mt-2 left-0 right-0 bg-white border-2 border-gray-200 rounded-2xl shadow-2xl z-50 overflow-hidden"
    >
      <!-- ส่วนหัว -->
      <div class="sticky top-0 bg-gradient-to-r from-yellow-500 to-yellow-600 text-white px-4 py-3 flex justify-between items-center">
        <span class="font-semibold">เลือกวิชา</span>
        <button
          v-if="modelValue"
          type="button"
          @click="clearSelection"
          class="text-xs bg-white text-yellow-600 px-3 py-1 rounded-full hover:bg-gray-100 transition-all font-semibold"
        >
          ล้าง
        </button>
      </div>

      <!-- รายการวิชา -->
      <div class="p-2 max-h-64 overflow-y-auto">
        <button
          v-for="subject in subjects"
          :key="subject"
          type="button"
          @click="selectSubject(subject)"
          class="w-full flex items-center gap-3 px-4 py-3 hover:bg-yellow-50 rounded-xl transition-all text-left group"
          :class="{ 'bg-yellow-100': modelValue === subject }"
        >
          <div 
            class="w-5 h-5 rounded-full border-2 flex items-center justify-center transition-all"
            :class="modelValue === subject 
              ? 'border-yellow-600 bg-yellow-600' 
              : 'border-gray-300 group-hover:border-yellow-400'"
          >
            <svg 
              v-if="modelValue === subject"
              xmlns="http://www.w3.org/2000/svg" 
              class="h-3 w-3 text-white" 
              viewBox="0 0 20 20" 
              fill="currentColor"
            >
              <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd" />
            </svg>
          </div>
          <span class="text-gray-700 font-medium">{{ subject }}</span>
        </button>
      </div>

      <!-- ปุ่มล่าง -->
      <div class="sticky bottom-0 bg-white border-t-2 border-gray-200 px-4 py-3 flex gap-2">
        <button
          type="button"
          @click="showDropdown = false"
          class="flex-1 px-4 py-2 bg-gray-200 text-gray-700 rounded-full hover:bg-gray-300 transition-all font-semibold"
        >
          ปิด
        </button>
        <button
          type="button"
          @click="showDropdown = false"
          class="flex-1 px-4 py-2 bg-gradient-to-r from-yellow-500 to-yellow-600 text-white rounded-full hover:from-yellow-600 hover:to-yellow-700 transition-all font-semibold shadow-md"
        >
          ตกลง
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: { type: String, default: '' },
  error: { type: String, default: '' }
})

const emit = defineEmits(['update:modelValue', 'clear-error'])

const showDropdown = ref(false)

const subjects = ['คณิตศาสตร์', 'ฟิสิกส์', 'เคมี', 'ชีววิทยา', 'ภาษาต่างประเทศ']

const selectSubject = (subject) => {
  emit('update:modelValue', subject)
  emit('clear-error')
  showDropdown.value = false
}

const clearSelection = () => {
  emit('update:modelValue', '')
  emit('clear-error')
}

// ปิด dropdown เมื่อคลิกข้างนอก
const handleClickOutside = (event) => {
  const dropdown = event.target.closest('.relative')
  if (!dropdown && showDropdown.value) {
    showDropdown.value = false
  }
}

watch(showDropdown, (newVal) => {
  if (newVal) {
    document.addEventListener('click', handleClickOutside)
  } else {
    document.removeEventListener('click', handleClickOutside)
  }
})
</script>