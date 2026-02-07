<template>
  <div class="grid grid-cols-1 gap-6">
    <div class="bg-gradient-to-br from-blue-50 via-indigo-50 to-purple-50 border-2 border-blue-200 p-6 rounded-2xl shadow-lg">
      <div class="flex items-center gap-3 mb-6">
        <div class="p-3 bg-gradient-to-br from-blue-600 to-indigo-600 rounded-xl shadow-md">
          <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
          </svg>
        </div>
        <h3 class="font-bold text-2xl bg-gradient-to-r from-blue-700 to-indigo-700 bg-clip-text text-transparent">
          เลือกคลาสที่ต้องการ
        </h3>
      </div>
      
      <div v-if="isLoadingOptions" class="text-center py-12">
        <div class="inline-block relative">
          <div class="animate-spin rounded-full h-16 w-16 border-4 border-blue-200"></div>
          <div class="absolute top-0 left-0 animate-spin rounded-full h-16 w-16 border-4 border-transparent border-t-blue-600"></div>
        </div>
        <p class="text-sm text-gray-600 mt-4 font-medium">กำลังโหลดตัวเลือก...</p>
      </div>

      <div v-else class="space-y-4">
        <div v-for="(cls, index) in modelValue" :key="index" 
          class="group relative overflow-hidden grid grid-cols-1 md:grid-cols-6 gap-4 p-5 rounded-xl border-2 transition-all duration-300 hover:shadow-xl"
          :class="cls.id 
            ? 'bg-gradient-to-br from-gray-50 to-gray-100 border-gray-300 shadow-md' 
            : 'bg-white border-blue-200 hover:border-blue-400 shadow-md hover:-translate-y-1'">
          
          <!-- Decorative gradient overlay -->
          <div class="absolute inset-0 bg-gradient-to-r from-blue-500/5 to-purple-500/5 opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none"></div>
          
          <div class="relative z-10 grid grid-cols-1 md:grid-cols-6 gap-4 col-span-full">
            <!-- เลือกวิชา -->
            <div class="space-y-2">
              <label class="flex items-center gap-2 font-bold text-sm text-gray-700">
                <span class="w-1.5 h-1.5 rounded-full bg-blue-600"></span>
                วิชา
              </label>
              <select 
                :value="cls.subjectId" 
                @change="updateClass(index, 'subjectId', $event.target.value)"
                :disabled="!!cls.id"
                class="w-full px-4 py-2.5 border-2 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-sm font-medium transition-all duration-200 shadow-sm hover:shadow-md"
                :class="cls.id 
                  ? 'bg-gray-100 border-gray-300 cursor-not-allowed text-gray-500' 
                  : 'bg-white border-gray-200 hover:border-blue-300'"
              >
                <option disabled value="">-- เลือกวิชา --</option>
                <option 
                  v-for="subject in subjects" 
                  :key="subject.id" 
                  :value="subject.id"
                >
                  {{ subject.subjectName }}
                </option>
              </select>
            </div>

            <!-- เลือกประเภทคลาส -->
            <div class="space-y-2">
              <label class="flex items-center gap-2 font-bold text-sm text-gray-700">
                <span class="w-1.5 h-1.5 rounded-full bg-indigo-600"></span>
                ประเภทคลาส
              </label>
              <select 
                :value="cls.classType" 
                @change="handleClassTypeChange(index, $event.target.value)"
                :disabled="!!cls.id"
                class="w-full px-4 py-2.5 border-2 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-sm font-medium transition-all duration-200 shadow-sm hover:shadow-md"
                :class="cls.id 
                  ? 'bg-gray-100 border-gray-300 cursor-not-allowed text-gray-500' 
                  : 'bg-white border-gray-200 hover:border-blue-300'"
              >
                <option disabled value="">-- เลือกประเภท --</option>
                <option value="MONTH">รายเดือน</option>
                <option value="HOUR">รายชั่วโมง</option>
              </select>
            </div>

            <!-- เลือกโหมด -->
            <div v-if="cls.classType" class="space-y-1">
              <label class="flex items-center gap-2 font-bold text-sm text-gray-700">
                <span class="w-1.5 h-1.5 rounded-full bg-purple-600"></span>
                โหมด
              </label>
              <div class="flex gap-2 pt-1">
                <label class="flex-1 relative cursor-pointer group" :class="{'opacity-50 cursor-not-allowed': cls.id}">
                  <input 
                    type="radio" 
                    :name="'mode-' + index"
                    :value="'GROUP'" 
                    :checked="cls.mode === 'GROUP'"
                    @change="handleModeChange(index, 'GROUP')"
                    :disabled="!!cls.id"
                    class="peer sr-only"
                  />
                  <div class="px-4 py-2.5 border-2 rounded-xl text-center transition-all duration-200 shadow-sm hover:shadow-md peer-checked:bg-gradient-to-br peer-checked:from-blue-600 peer-checked:to-indigo-600 peer-checked:border-blue-600 peer-checked:text-white peer-checked:shadow-lg peer-checked:scale-105"
                       :class="cls.id ? 'border-gray-300 bg-gray-100' : 'border-gray-200 bg-white hover:border-blue-300'">
                    <span class="text-sm font-bold">กลุ่ม</span>
                  </div>
                </label>
                <label class="flex-1 relative cursor-pointer group" :class="{'opacity-50 cursor-not-allowed': cls.id}">
                  <input 
                    type="radio" 
                    :name="'mode-' + index"
                    :value="'PRIVATE'" 
                    :checked="cls.mode === 'PRIVATE'"
                    @change="handleModeChange(index, 'PRIVATE')"
                    :disabled="cls.classType === 'MONTH' || !!cls.id"
                    class="peer sr-only"
                  />
                  <div class="px-4 py-2.5 border-2 rounded-xl text-center transition-all duration-200 shadow-sm hover:shadow-md peer-checked:bg-gradient-to-br peer-checked:from-purple-600 peer-checked:to-pink-600 peer-checked:border-purple-600 peer-checked:text-white peer-checked:shadow-lg peer-checked:scale-105"
                       :class="cls.id ? 'border-gray-300 bg-gray-100' : 'border-gray-200 bg-white hover:border-purple-300'">
                    <span class="text-sm font-bold">เดี่ยว</span>
                  </div>
                </label>
              </div>
            </div>

            <!-- ถ้ารายเดือน + กลุ่ม → เลือกตาราง -->
            <div v-if="cls.classType === 'MONTH' && cls.mode === 'GROUP'" class="space-y-2 pl-3 w-30">
              <label class="flex items-center gap-2 font-bold text-sm text-gray-700">
                <span class="w-1.5 h-1.5 rounded-full bg-green-600"></span>
                เวลาเรียน
              </label>
              <select 
                :value="cls.schedule" 
                @change="handleScheduleChange(index, $event.target.value)"
                :disabled="!!cls.id"
                class="w-full px-4 py-2.5 border-2 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-sm font-medium transition-all duration-200 shadow-sm hover:shadow-md"
                :class="cls.id 
                  ? 'bg-gray-100 border-gray-300 cursor-not-allowed text-gray-500' 
                  : 'bg-white border-gray-200 hover:border-blue-300'"
              >
                <option disabled value="">-- เลือกเวลา --</option>
                <option 
                  v-for="monthly in monthlySubtypes" 
                  :key="monthly.id" 
                  :value="monthly.subtypeName"
                >
                  {{ monthly.subtypeName }}
                </option>
              </select>
            </div>

            <!-- ถ้ารายชั่วโมง + กลุ่ม → เลือกประเภทกลุ่ม -->
            <div v-if="cls.classType === 'HOUR' && cls.mode === 'GROUP'" class="space-y-2 pl-3">
              <label class="flex items-center gap-2 font-bold text-sm text-gray-700">
                <span class="w-1.5 h-1.5 rounded-full bg-teal-600"></span>
                ประเภทกลุ่ม
              </label>
              <select 
                :value="cls.groupType" 
                @change="handleGroupTypeChange(index, $event.target.value)"
                :disabled="!!cls.id"
                class="w-full px-4 py-2.5 border-2 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-sm font-medium transition-all duration-200 shadow-sm hover:shadow-md"
                :class="cls.id 
                  ? 'bg-gray-100 border-gray-300 cursor-not-allowed text-gray-500' 
                  : 'bg-white border-gray-200 hover:border-blue-300'"
              >
                <option disabled value="">-- เลือกประเภท --</option>
                <option 
                  v-for="hourly in hourlyGroupSubtypes" 
                  :key="hourly.id" 
                  :value="hourly.subtypeName"
                >
                  {{ hourly.subtypeName }}
                </option>
              </select>
            </div>

            <!-- ถ้ารายชั่วโมง + เดี่ยว ให้เลือกครู (ไม่บังคับ) -->
            <div v-if="cls.classType === 'HOUR' && cls.mode === 'PRIVATE'" class="space-y-2 pl-3">
              <label class="flex items-center gap-2 font-bold text-sm text-gray-700">
                <span class="w-1.5 h-1.5 rounded-full bg-amber-600"></span>
                ครูผู้สอน
              </label>
              <select 
                :value="cls.tutorId" 
                @change="updateClass(index, 'tutorId', $event.target.value)"
                :disabled="!!cls.id"
                class="w-full px-4 py-2.5 border-2 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-sm font-medium transition-all duration-200 shadow-sm hover:shadow-md"
                :class="cls.id 
                  ? 'bg-gray-100 border-gray-300 cursor-not-allowed text-gray-500' 
                  : 'bg-white border-gray-200 hover:border-blue-300'"
              >
                <option value="">-- ยังไม่ระบุ --</option>
                <option 
                  v-for="tutor in tutors" 
                  :key="tutor.id" 
                  :value="tutor.id"
                >
                  {{ tutor.nickname || tutor.username }}
                </option>
              </select>
            </div>

            <!-- ถ้าเป็นรายชั่วโมง ให้แสดงจำนวนชั่วโมง (อ่านอย่างเดียว ถ้าดึงมาได้) -->
            <div v-if="cls.classType === 'HOUR'" class="space-y-2">
              <label class="flex items-center gap-2 font-bold text-sm text-gray-700">
                <span class="w-1.5 h-1.5 rounded-full bg-rose-600"></span>
                จำนวนชั่วโมง
              </label>
              
              <!-- ถ้ามี hours แล้ว (จากระบบหรือจาก existing class) แสดงแบบ readonly -->
              <div v-if="cls.hours && cls.autoFilled" class="relative">
                <input 
                  :value="cls.hours" 
                  type="number" 
                  readonly
                  class="w-full px-4 py-2.5 border-2 rounded-xl text-sm font-medium bg-green-50 border-green-300 text-green-700 cursor-not-allowed"
                  placeholder="ชม."
                />
                <div class="absolute right-3 top-1/2 -translate-y-1/2 flex items-center gap-1 text-green-600">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                  </svg>
                  <span class="text-xs font-semibold">อัตโนมัติ</span>
                </div>
              </div>
              
              <!-- ถ้ายังไม่มี hours ให้กรอกเอง -->
              <input 
                v-else
                :value="cls.hours" 
                @input="updateClass(index, 'hours', $event.target.value)"
                :disabled="!!cls.id"
                type="number" 
                min="1" 
                step="0.5"
                class="w-full px-4 py-2.5 border-2 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-sm font-medium transition-all duration-200 shadow-sm hover:shadow-md" 
                :class="cls.id 
                  ? 'bg-gray-100 border-gray-300 cursor-not-allowed text-gray-500' 
                  : 'bg-white border-gray-200 hover:border-blue-300'"
                placeholder="กรอกจำนวนชั่วโมง"
              />
            </div>

            <!-- ลบ / ล็อค -->
            <div class="flex items-end w-25">
              <button 
                v-if="!hideDeleteButton || !cls.id"
                type="button" 
                @click="removeClass(index)" 
                class="w-full px-4 py-2.5 bg-gradient-to-r from-red-500 to-red-600 text-white rounded-xl hover:from-red-600 hover:to-red-700 transition-all duration-200 text-sm font-bold flex items-center justify-center gap-2 shadow-md hover:shadow-lg hover:-translate-y-0.5"
              >
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                </svg>
                ลบ
              </button>
              <div v-else-if="hideDeleteButton && cls.id" class="w-full px-4 py-2.5 bg-gradient-to-br from-gray-100 to-gray-200 text-gray-600 rounded-xl text-sm text-center border-2 border-gray-300 font-bold shadow-sm">
                <svg class="w-4 h-4 inline-block mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                </svg>
                ล็อค
              </div>
            </div>
          </div>
        </div>

        <button 
          type="button" 
          @click="addNewClass" 
          class="group px-6 py-3.5 bg-gradient-to-r from-blue-600 to-indigo-600 text-white rounded-xl hover:from-blue-700 hover:to-indigo-700 transition-all duration-200 flex items-center justify-center gap-3 font-bold shadow-lg hover:shadow-xl hover:-translate-y-1 w-full md:w-auto"
        >
          <div class="p-1.5 bg-white/20 rounded-lg group-hover:bg-white/30 transition-colors">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
            </svg>
          </div>
          <span class="text-base">เพิ่มคลาส</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '@/api.js';

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  },
  hideDeleteButton: {
    type: Boolean,
    default: false
  },
  studentGradeId: {
    type: [Number, null],
    default: null
  }
});

const emit = defineEmits(['update:modelValue', 'validate']);

// ข้อมูลจาก API
const subjects = ref([]);
const monthlySubtypes = ref([]);
const hourlyGroupSubtypes = ref([]);
const tutors = ref([]);
const isLoadingOptions = ref(false);

// เก็บข้อมูลคลาสที่มีอยู่ทั้งหมดในระบบ
const existingClasses = ref({
  monthly: [],
  hourlyGroup: [],
  hourlyIndividual: []
});

// โหลดข้อมูลตัวเลือกทั้งหมด
const loadOptions = async () => {
  isLoadingOptions.value = true;
  try {
    const [subjectsRes, monthlyRes, hourlyRes, tutorsRes, monthlyClassesRes, hourlyGroupClassesRes] = await Promise.all([
      api.get('/subjects/all'),
      api.get('/monthly-subtypes/all'),
      api.get('/hourly-group-subtypes/all'),
      api.get('/tutors?active=true'),
      api.get('/monthly-classes').catch(() => ({ data: [] })),
      api.get('/hourly-group-classes').catch(() => ({ data: [] }))
    ]);

    // กรองเฉพาะที่ isActive: true
    subjects.value = subjectsRes.data.filter(item => item.isActive);
    monthlySubtypes.value = monthlyRes.data.filter(item => item.isActive);
    hourlyGroupSubtypes.value = hourlyRes.data.filter(item => item.isActive);
    tutors.value = tutorsRes.data;
    
    // เก็บข้อมูลคลาสที่มีอยู่
    existingClasses.value.monthly = monthlyClassesRes.data || [];
    existingClasses.value.hourlyGroup = hourlyGroupClassesRes.data || [];
    
    console.log('Loaded existing classes:', existingClasses.value);
  } catch (error) {
    console.error('Error loading options:', error);
  } finally {
    isLoadingOptions.value = false;
  }
};

// ฟังก์ชันค้นหาคลาสที่ตรงกัน (เพิ่มการเช็ค gradeId)
const findMatchingClass = (subjectId, classType, mode, scheduleOrGroupType, gradeId) => {
  if (classType === 'MONTH') {
    // ค้นหาใน monthly classes
    return existingClasses.value.monthly.find(cls => 
      cls.subjectId === parseInt(subjectId) &&
      cls.subtypeName === scheduleOrGroupType &&
      cls.gradeId === parseInt(gradeId)
    );
  } else if (classType === 'HOUR' && mode === 'GROUP') {
    // ค้นหาใน hourly group classes
    return existingClasses.value.hourlyGroup.find(cls => 
      cls.subjectId === parseInt(subjectId) &&
      cls.subtypeName === scheduleOrGroupType &&
      cls.gradeId === parseInt(gradeId)
    );
  }
  return null;
};

// ดึง gradeId ของนักเรียน
const getStudentGradeId = () => {
  return props.studentGradeId;
};

// อัปเดต hours อัตโนมัติเมื่อมีการเปลี่ยนแปลง
const autoFillHours = (index) => {
  const cls = props.modelValue[index];
  
  // ตรวจสอบว่าเป็นคลาสรายชั่วโมงและมีข้อมูลครบหรือไม่
  if (cls.classType === 'HOUR' && cls.subjectId && cls.mode) {
    let scheduleOrGroupType = null;
    
    if (cls.mode === 'GROUP' && cls.groupType) {
      scheduleOrGroupType = cls.groupType;
    } else if (cls.mode === 'PRIVATE') {
      // สำหรับ private ไม่มี hours ที่ระบุไว้ล่วงหน้า
      return;
    }
    
    if (scheduleOrGroupType) {
      // ⚠️ สำคัญ: ต้องมี gradeId จากนักเรียนด้วย
      // ดึง gradeId จาก parent component (EditStudentModal)
      const studentGradeId = getStudentGradeId();
      
      if (!studentGradeId) {
        console.warn('Cannot auto-fill hours: Student gradeId not found');
        return;
      }
      
      const matchingClass = findMatchingClass(
        cls.subjectId, 
        cls.classType, 
        cls.mode, 
        scheduleOrGroupType,
        studentGradeId // เพิ่มการเช็ค gradeId
      );
      
      if (matchingClass && matchingClass.hoursTarget) {
        const updatedClasses = [...props.modelValue];
        updatedClasses[index] = {
          ...updatedClasses[index],
          hours: matchingClass.hoursTarget,
          autoFilled: true // flag เพื่อบอกว่า hours ถูกเติมอัตโนมัติ
        };
        emit('update:modelValue', updatedClasses);
        emit('validate');
      } else {
        console.log('ℹ️ No matching class found for:', {
          subjectId: cls.subjectId,
          type: scheduleOrGroupType,
          gradeId: studentGradeId
        });
      }
    }
  }
};

const updateClass = (index, field, value) => {
  const updatedClasses = [...props.modelValue];
  const parsedValue = (field === 'hours' || field === 'tutorId' || field === 'subjectId') 
    ? parseInt(value) || null 
    : value;
  
  updatedClasses[index] = {
    ...updatedClasses[index],
    [field]: parsedValue
  };
  
  // ถ้าเปลี่ยน hours manually ให้ลบ flag autoFilled
  if (field === 'hours') {
    updatedClasses[index].autoFilled = false;
  }
  
  emit('update:modelValue', updatedClasses);
  emit('validate');
  
  // ลองหา hours อัตโนมัติหลังจากอัปเดต subject
  if (field === 'subjectId') {
    setTimeout(() => autoFillHours(index), 100);
  }
};

const handleClassTypeChange = (index, value) => {
  const updatedClasses = [...props.modelValue];
  updatedClasses[index] = {
    ...updatedClasses[index],
    classType: value,
    mode: value === 'MONTH' ? 'GROUP' : null,
    schedule: null,
    groupType: null,
    tutorId: null,
    hours: null,
    autoFilled: false
  };
  emit('update:modelValue', updatedClasses);
  emit('validate');
};

const handleModeChange = (index, value) => {
  const updatedClasses = [...props.modelValue];
  updatedClasses[index] = {
    ...updatedClasses[index],
    mode: value,
    schedule: null,
    groupType: null,
    tutorId: null,
    hours: null,
    autoFilled: false
  };
  emit('update:modelValue', updatedClasses);
  emit('validate');
};

const handleScheduleChange = (index, value) => {
  updateClass(index, 'schedule', value);
  setTimeout(() => autoFillHours(index), 100);
};

const handleGroupTypeChange = (index, value) => {
  updateClass(index, 'groupType', value);
  setTimeout(() => autoFillHours(index), 100);
};

const addNewClass = () => {
  const updatedClasses = [
    ...props.modelValue,
    {
      subjectId: '',
      classType: '',
      mode: '',
      schedule: null,
      groupType: null,
      tutorId: null,
      hours: null,
      autoFilled: false
    }
  ];
  emit('update:modelValue', updatedClasses);
};

const removeClass = (index) => {
  const updatedClasses = props.modelValue.filter((_, i) => i !== index);
  emit('update:modelValue', updatedClasses);
  emit('validate');
};

onMounted(() => {
  loadOptions();
});
</script>
