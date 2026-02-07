<template>
  <div class="relative w-full md:w-auto">
    <button
      @click="showDropdown = !showDropdown"
      class="px-6 py-3 bg-gradient-to-r from-green-500 to-emerald-600 text-white font-semibold rounded-full shadow-md hover:shadow-lg hover:from-green-600 hover:to-emerald-700 transition-all flex items-center gap-2 w-full md:w-auto justify-center"
    >
      <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
      </svg>
      เลือกเดือน
    </button>

    <div
      v-if="showDropdown"
      class="absolute top-full mt-2 right-0 bg-white border-2 border-gray-200 rounded-2xl shadow-2xl z-50 w-72 max-h-96 overflow-auto"
    >
      <!-- ส่วนหัว -->
      <div
        class="sticky top-0 bg-gradient-to-r from-green-500 to-emerald-600 text-white px-4 py-3 rounded-t-2xl flex justify-between items-center min-h-[80px]"
      >
        <span class="font-semibold">เลือกเดือน</span>
        <button
          v-if="internalValue.length > 0"
          @click="clearSelection"
          class="text-xs bg-white text-green-600 px-3 py-1 rounded-full hover:bg-gray-100 transition-all font-semibold"
        >
          ล้าง
        </button>
      </div>

      <!-- รายการเดือน -->
      <div class="p-2">
        <label
          v-for="(m, i) in thaiMonths"
          :key="i"
          class="flex items-center gap-3 px-4 py-3 hover:bg-green-50 rounded-xl cursor-pointer transition-all group"
        >
          <input
            type="checkbox"
            :value="i + 1"
            v-model="internalValue"
            @change="onMonthChange"
            class="w-5 h-5 text-green-600 border-2 border-gray-300 rounded focus:ring-0 focus:ring-offset-0 cursor-pointer"
          />
          <span class="text-gray-700 font-medium">{{ m }}</span>
        </label>
      </div>

      <!-- ปุ่มล่าง -->
      <div class="sticky bottom-0 bg-white border-t-2 border-gray-200 px-4 py-3 rounded-b-2xl flex gap-2">
        <button
          @click="showDropdown = false"
          class="flex-1 px-4 py-2 bg-gray-200 text-gray-700 rounded-full hover:bg-gray-300 transition-all font-semibold"
        >
          ปิด
        </button>
        <button
          @click="showDropdown = false"
          class="flex-1 px-4 py-2 bg-gradient-to-r from-green-500 to-emerald-600 text-white rounded-full hover:from-green-600 hover:to-emerald-700 transition-all font-semibold shadow-md"
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
  modelValue: { type: Array, default: () => [] }
})
const emit = defineEmits(['update:modelValue', 'apply'])

const showDropdown = ref(false)
const internalValue = ref([...props.modelValue])

const thaiMonths = [
  'มกราคม', 'กุมภาพันธ์', 'มีนาคม', 'เมษายน', 'พฤษภาคม', 'มิถุนายน',
  'กรกฎาคม', 'สิงหาคม', 'กันยายน', 'ตุลาคม', 'พฤศจิกายน', 'ธันวาคม'
]

// sync ค่าเมื่อ parent เปลี่ยน
watch(
  () => props.modelValue,
  val => {
    internalValue.value = [...val]
  }
)

// อัปเดตผลลัพธ์ทันทีเมื่อมีการติ๊ก / ยกเลิกติ๊กเดือน
const onMonthChange = () => {
  emit('update:modelValue', [...internalValue.value])
  emit('apply')
}

// ล้างทั้งหมด + รีเฟรชผลลัพธ์ทันที
const clearSelection = () => {
  internalValue.value = []
  emit('update:modelValue', [])
  emit('apply')
}
</script>
