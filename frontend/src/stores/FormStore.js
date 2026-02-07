import { defineStore } from 'pinia'
import api from '../api'

export const useFormStore = defineStore('formStore', {
  state: () => ({
    forms: [],
    loading: false,
    error: null,
  }),

  actions: {
    async fetchForms(tutorId = null) {
      this.loading = true
      this.error = null

      try {
        // ถ้ามี tutorId (admin mode) → ใช้ /hour-forms?tutorId=xxx
        // ถ้าไม่มี (tutor mode) → ใช้ /hour-forms/my-forms
        let res
        if (tutorId) {
          res = await api.get('/hour-forms', { params: { tutorId } })
        } else {
          res = await api.get('/hour-forms/my-forms')
        }
        
        this.forms = res.data || []
      } catch (err) {
        console.error('Error fetching forms:', err)
        this.error = err.message || 'เกิดข้อผิดพลาดในการดึงข้อมูล'
      } finally {
        this.loading = false
      }
    },

    async fetchAllForms(tutorId = null) {
      this.loading = true
      this.error = null

      try {
        const endpoints = [
          tutorId 
            ? api.get('/hour-forms', { params: { tutorId } }) 
            : api.get('/hour-forms/my-forms'),
          tutorId 
            ? api.get('/math-forms', { params: { tutorId } }) 
            : api.get('/math-forms/my-forms'),
          tutorId 
            ? api.get('/science-forms', { params: { tutorId } }) 
            : api.get('/science-forms/my-forms')
        ]

        const [hourRes, mathRes, scienceRes] = await Promise.all(endpoints)
        
        this.forms = [
          ...(hourRes.data || []).map(f => ({ ...f, formType: 'hour' })),
          ...(mathRes.data || []).map(f => ({ ...f, formType: 'math' })),
          ...(scienceRes.data || []).map(f => ({ ...f, formType: 'science' }))
        ]
      } catch (err) {
        console.error('Error fetching all forms:', err)
        this.error = err.message || 'เกิดข้อผิดพลาดในการดึงข้อมูล'
      } finally {
        this.loading = false
      }
    },
    
    async fetchFormHistory(formType, id) {
      this.loading = true
      this.error = null

      try {
        const endpoints = {
          hour: `/hour-forms/${id}/history`,
          math: `/math-forms/${id}/history`,
          science: `/science-forms/${id}/history`
        }
        const res = await api.get(endpoints[formType])
        return res.data
      } catch (err) {
        this.error = err.message || 'โหลดประวัติล้มเหลว'
        return []
      } finally {
        this.loading = false
      }
    },
  },
})
