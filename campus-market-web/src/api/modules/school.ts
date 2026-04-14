import request from '../request'
import type { Campus, School } from '@/types'

export const schoolApi = {
  listSchools: () =>
    request.get<any, { data: School[] }>('/schools'),

  listCampuses: (schoolId: number) =>
    request.get<any, { data: Campus[] }>('/campuses', { params: { schoolId } }),
}
