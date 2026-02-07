<template>
  <div class="relative w-full md:w-auto">
    <button
      @click="showDropdown = !showDropdown"
      class="px-6 py-3 bg-gradient-to-r from-blue-500 to-indigo-600 text-white font-semibold rounded-full shadow-md hover:shadow-lg hover:from-blue-600 hover:to-indigo-700 transition-all flex items-center gap-2 w-full md:w-auto justify-center"
    >
      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
          d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
      </svg>
      เลือกช่วงเวลา
    </button>

    <div
      v-if="showDropdown"
      class="absolute top-full mt-2 right-0 bg-white border-2 border-gray-200 rounded-2xl shadow-2xl z-50 w-80"
    >
      <!-- ส่วนหัว -->
      <div
        class="sticky top-0 bg-gradient-to-r from-blue-500 to-indigo-600 text-white px-4 py-3 rounded-t-2xl flex justify-between items-center">
        <span class="font-semibold">เลือกช่วงเวลา</span>
        <button
          v-if="hasDateRange"
          @click="clearDates"
          class="text-xs bg-white text-blue-600 px-3 py-1 rounded-full hover:bg-gray-100 transition-all font-semibold"
        >
          ล้าง
        </button>
      </div>

      <!-- เนื้อหา -->
      <div class="p-4 space-y-4">
        <!-- วันที่เริ่มต้น -->
        <div>
          <label class="block text-sm font-semibold text-gray-700 mb-2">
            วันที่เริ่มต้น
          </label>
          <input
            type="date"
            v-model="startDate"
            :max="endDate || undefined"
            @change="autoApply"
            class="w-full px-4 py-2 border-2 border-gray-300 rounded-lg focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all"
          />
        </div>

        <!-- วันที่สิ้นสุด -->
        <div>
          <label class="block text-sm font-semibold text-gray-700 mb-2">
            วันที่สิ้นสุด
          </label>
          <input
            type="date"
            v-model="endDate"
            :min="startDate || undefined"
            @change="autoApply"
            class="w-full px-4 py-2 border-2 border-gray-300 rounded-lg focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-all"
          />
        </div>

        <!-- แสดงช่วงที่เลือก -->
        <div v-if="hasDateRange" class="bg-blue-50 border border-blue-200 rounded-lg p-3">
          <p class="text-sm text-blue-800 font-medium text-center">
            {{ formatDateThai(startDate) }}
            <span class="mx-2">→</span>
            {{ formatDateThai(endDate) }}
          </p>
        </div>
      </div>

      <!-- ปุ่มล่าง -->
      <div
        class="sticky bottom-0 bg-white border-t-2 border-gray-200 px-4 py-3 rounded-b-2xl flex gap-2">
        <button
          @click="cancel"
          class="flex-1 px-4 py-2 bg-gray-200 text-gray-700 rounded-full hover:bg-gray-300 transition-all font-semibold"
        >
          ปิด
        </button>
        <button
          @click="closeOnly"
          class="flex-1 px-4 py-2 bg-gradient-to-r from-blue-500 to-indigo-600 text-white rounded-full hover:from-blue-600 hover:to-indigo-700 transition-all font-semibold shadow-md"
        >
          ตกลง
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({ startDate: null, endDate: null })
  }
})

const emit = defineEmits(['update:modelValue', 'apply'])

const showDropdown = ref(false)
const startDate = ref(props.modelValue?.startDate || null)
const endDate = ref(props.modelValue?.endDate || null)

const hasDateRange = computed(() => startDate.value || endDate.value)

const formatDateThai = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('th-TH', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const clearDates = () => {
  startDate.value = null
  endDate.value = null
  emit('update:modelValue', { startDate: null, endDate: null })
  emit('apply')
}

const cancel = () => {
  startDate.value = props.modelValue?.startDate || null
  endDate.value = props.modelValue?.endDate || null
  showDropdown.value = false
}

const closeOnly = () => {
  showDropdown.value = false
}

// เมื่อผู้ใช้เปลี่ยนวันที่ ให้ emit และ apply อัตโนมัติ
const autoApply = () => {
  emit('update:modelValue', {
    startDate: startDate.value,
    endDate: endDate.value
  })
  emit('apply')
}

// ถ้าค่าจาก parent เปลี่ยน ให้ sync กลับ
watch(() => props.modelValue, (newVal) => {
  startDate.value = newVal?.startDate || null
  endDate.value = newVal?.endDate || null
})
</script>
