<template>
  <div v-if="show" class="fixed inset-0 z-50 overflow-y-auto" @click="handleBackdropClick">
    <div class="fixed inset-0 bg-black/60 backdrop-blur-sm transition-opacity"></div>
    <div class="flex min-h-full items-center justify-center p-4">
      <div class="relative w-full max-w-6xl bg-gradient-to-br from-slate-900 via-blue-900 to-slate-900 rounded-3xl shadow-2xl transform transition-all" @click.stop>
        <button @click="$emit('close')" class="absolute w-12 h-10 flex items-center justify-center top-6 right-6 z-100 bg-white/10 hover:bg-white/20 rounded-full transition-all group">
          <svg class="w-6 h-6 text-white group-hover:rotate-90 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
          </svg>
        </button>

        <div v-if="isLoading" class="flex items-center justify-center py-24">
          <div class="text-center">
            <div class="inline-block animate-spin rounded-full h-16 w-16 border-b-4 border-blue-400"></div>
            <p class="mt-4 text-gray-300 text-lg">กำลังโหลดข้อมูล...</p>
          </div>
        </div>

        <div v-else-if="error" class="p-8">
          <div class="bg-red-500/20 border border-red-500 rounded-2xl p-6">
            <div class="flex items-center gap-3">
              <svg class="w-6 h-6 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <p class="text-red-400 font-semibold">{{ error }}</p>
            </div>
          </div>
        </div>

        <div v-else class="relative z-10 max-h-[85vh] overflow-y-auto p-6">
          <div class="bg-gradient-to-r from-blue-600 to-indigo-700 rounded-3xl p-6 shadow-2xl mb-4">
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-4">
                
                <!-- Avatar with Gradient ICON -->
                <div class="w-20 h-20 bg-white rounded-full flex items-center justify-center shadow-lg">
                  <svg xmlns="http://www.w3.org/2000/svg" class="w-8 h-8" fill="none" viewBox="0 0 24 24">
                    <defs>
                      <linearGradient id="bluePurple" x1="0%" y1="0%" x2="100%" y2="100%">
                        <stop offset="0%" stop-color="#1e3a8a"/>
                        <stop offset="100%" stop-color="#6d28d9"/>
                      </linearGradient>
                    </defs>
                    
                    <path
                      stroke="url(#bluePurple)"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="3"
                      d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                    />
                  </svg>
                </div>

                <!-- Tutor Info -->
                <div class="text-white">
                  <h3 class="text-2xl font-bold">{{ tutor.nickname || tutor.firstName }}</h3>
                  <p class="text-sm text-blue-100">{{ tutor.firstName }} {{ tutor.lastName || '' }}</p>
                </div>

              </div>
            </div>
          </div>

          <div class="mb-6">
            <div class="flex items-center justify-between mb-4">
              <h4 class="text-lg font-bold text-white">คลาสที่สอนทั้งหมด</h4>
              <div class="text-sm text-gray-300"><span class="font-semibold text-blue-400">{{ activeClassCount }}</span> คลาสที่สอนอยู่</div>
            </div>

            <div v-if="monthlyClasses.length > 0" class="mb-4">
              <h5 class="text-sm text-gray-200 mb-2">คลาสรายเดือน</h5>
              <div class="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-4">
                <StudentClassCard v-for="c in monthlyClasses" :key="'m-'+c.id" :class-data="c" type="monthly" :read-only="true" />
              </div>
            </div>

            <div v-if="hourlyGroupClasses.length > 0" class="mb-4">
              <h5 class="text-sm text-gray-200 mb-2">คลาสรายชั่วโมงแบบกลุ่มรวม</h5>
              <div class="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-4">
                <StudentClassCard v-for="c in hourlyGroupClasses" :key="'hg-'+c.id" :class-data="c" type="hourly-group" :read-only="true" />
              </div>
            </div>

            <div v-if="hourlyIndividualClasses.length > 0" class="mb-4">
              <h5 class="text-sm text-gray-200 mb-2">คลาสรายชั่วโมงแบบ PV-เดี่ยว และ PV-กลุ่ม</h5>
              <div class="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-4">
                <StudentClassCard v-for="c in hourlyIndividualClasses" :key="'hi-'+c.id" :class-data="c" type="hourly-individual" :read-only="true" />
              </div>
            </div>

            <div v-if="!hasAnyClasses" class="text-center py-12 text-gray-400">ยังไม่มีคลาส</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import StudentClassCard from '@/components/StudentClassCard.vue';
import api from '@/api.js';

const props = defineProps({
  show: Boolean,
  tutorId: Number
});

const emit = defineEmits(['close', 'edit']);

const isLoading = ref(false);
const error = ref('');
const tutor = ref({});
const monthlyClasses = ref([]);
const hourlyGroupClasses = ref([]);
const hourlyIndividualClasses = ref([]);

const hasAnyClasses = computed(() => {
  return monthlyClasses.value.length > 0 || hourlyGroupClasses.value.length > 0 || hourlyIndividualClasses.value.length > 0;
});

const activeClassCount = computed(() => {
  return [...monthlyClasses.value, ...hourlyGroupClasses.value, ...hourlyIndividualClasses.value].filter(c => c.isActive).length;
});

const loadTutorDetails = async () => {
  if (!props.tutorId) return;
  isLoading.value = true;
  error.value = '';
  try {
    // Tutor basic info
    const tRes = await api.get(`/tutors/${props.tutorId}`);
    tutor.value = tRes.data || {};

    // Fetch classes by tutor from dedicated class endpoints
    const [mRes, hgRes, hiRes] = await Promise.all([
      api.get('/monthly-classes', { params: { tutorId: props.tutorId } }),
      api.get('/hourly-group-classes', { params: { tutorId: props.tutorId } }),
      api.get('/hourly-individual-classes', { params: { tutorId: props.tutorId } })
    ]);

    monthlyClasses.value = mRes.data || [];
    hourlyGroupClasses.value = hgRes.data || [];
    hourlyIndividualClasses.value = hiRes.data || [];
  } catch (err) {
    console.error('Error loading tutor details:', err);
    error.value = 'ไม่สามารถโหลดข้อมูลผู้สอนได้';
  } finally {
    isLoading.value = false;
  }
};

const handleEdit = () => {
  const tutorWithClasses = {
    ...tutor.value,
    classes: [
      ...monthlyClasses.value.map(c => ({ id: c.id, subjectId: c.subjectId, classTypeId: 1, classMode: 'GROUP', classSubtypeId: null })),
      ...hourlyGroupClasses.value.map(c => ({ id: c.id, subjectId: c.subjectId, classTypeId: 2, classMode: 'GROUP', classSubtypeId: null, hoursTarget: c.hoursTarget })),
      ...hourlyIndividualClasses.value.map(c => ({ id: c.id, subjectId: c.subjectId, classTypeId: 2, classMode: 'SINGLE', hoursTarget: c.hoursTarget }))
    ]
  };
  emit('edit', tutorWithClasses);
};

const handleBackdropClick = (e) => {
  if (e.target === e.currentTarget) emit('close');
};

watch(() => props.show, (v) => {
  if (v) {
    loadTutorDetails();
    document.body.style.overflow = 'hidden';
  } else {
    document.body.style.overflow = '';
  }
});
</script>

<style scoped>
.overflow-y-auto::-webkit-scrollbar { width: 8px; }
</style>
