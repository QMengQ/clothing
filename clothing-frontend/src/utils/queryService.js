import request from './request'

/**
 * 查询服务类
 * 用于处理各模块的查询逻辑
 */
class QueryService {
  /**
   * 构造函数
   * @param {string} baseUrl - 基础URL
   */
  constructor(baseUrl) {
    this.baseUrl = baseUrl
  }

  /**
   * 执行查询
   * @param {Object} queryParams - 查询参数
   * @param {string} queryParams.keyword - 搜索关键词
   * @param {Object} queryParams.filters - 筛选条件
   * @param {string} queryParams.sortBy - 排序字段
   * @param {number} queryParams.page - 当前页码
   * @param {number} queryParams.pageSize - 每页大小
   * @returns {Promise} 查询结果
   */
  async query(queryParams) {
    try {
      const response = await request.get(this.baseUrl, {
        params: {
          keyword: queryParams.keyword,
          filters: JSON.stringify(queryParams.filters),
          sortBy: queryParams.sortBy,
          page: queryParams.page,
          pageSize: queryParams.pageSize
        }
      })
      return response.data
    } catch (error) {
      console.error('查询失败:', error)
      // 当API调用失败时，回退到本地过滤逻辑
      return this.fallbackQuery(queryParams)
    }
  }

  /**
   * 本地回退查询逻辑
   * @param {Object} queryParams - 查询参数
   * @returns {Object} 模拟的查询结果
   */
  async fallbackQuery(queryParams) {
    try {
      // 首先获取所有数据
      let allData = []
      
      // 根据不同模块获取对应的数据
      switch (this.baseUrl) {
        case '/clothing':
          const clothingResponse = await request.get('/clothing/my')
          allData = clothingResponse.data || []
          break
        case '/location':
          const locationResponse = await request.get('/location')
          allData = locationResponse.data || []
          break
        case '/recycle':
          const recycleResponse = await request.get('/recycle/my')
          allData = recycleResponse.data || []
          break
        default:
          allData = []
      }
      
      // 应用关键词搜索
      if (queryParams.keyword) {
        const keyword = queryParams.keyword.toLowerCase()
        allData = allData.filter(item => {
          // 检查对象的所有字符串属性
          for (const key in item) {
            if (typeof item[key] === 'string' && item[key].toLowerCase().includes(keyword)) {
              return true
            }
          }
          return false
        })
      }
      
      // 应用筛选条件
      if (queryParams.filters) {
        for (const key in queryParams.filters) {
          const value = queryParams.filters[key]
          if (value) {
            allData = allData.filter(item => item[key] === value)
          }
        }
      }
      
      // 应用排序
      if (queryParams.sortBy) {
        allData.sort((a, b) => {
          const aValue = a[queryParams.sortBy]
          const bValue = b[queryParams.sortBy]
          
          if (typeof aValue === 'string' && typeof bValue === 'string') {
            return aValue.localeCompare(bValue)
          } else if (typeof aValue === 'number' && typeof bValue === 'number') {
            return aValue - bValue
          } else if (aValue instanceof Date && bValue instanceof Date) {
            return aValue - bValue
          } else if (aValue && bValue) {
            return String(aValue).localeCompare(String(bValue))
          } else {
            return 0
          }
        })
      }
      
      // 应用分页
      const page = queryParams.page || 1
      const pageSize = queryParams.pageSize || 10
      const start = (page - 1) * pageSize
      const end = start + pageSize
      const paginatedData = allData.slice(start, end)
      
      // 返回模拟的响应数据
      return {
        data: paginatedData,
        total: allData.length
      }
    } catch (error) {
      console.error('本地查询失败:', error)
      throw error
    }
  }

  /**
   * 快速查询（无分页）
   * @param {Object} queryParams - 查询参数
   * @returns {Promise} 查询结果
   */
  async quickQuery(queryParams) {
    try {
      const response = await request.get(`${this.baseUrl}/quick`, {
        params: {
          keyword: queryParams.keyword,
          filters: JSON.stringify(queryParams.filters),
          sortBy: queryParams.sortBy
        }
      })
      return response.data
    } catch (error) {
      console.error('快速查询失败:', error)
      // 当API调用失败时，回退到本地过滤逻辑（无分页）
      try {
        // 首先获取所有数据
        let allData = []
        
        // 根据不同模块获取对应的数据
        switch (this.baseUrl) {
          case '/clothing':
            const clothingResponse = await request.get('/clothing/my')
            allData = clothingResponse.data || []
            break
          case '/location':
            const locationResponse = await request.get('/location')
            allData = locationResponse.data || []
            break
          case '/recycle':
            const recycleResponse = await request.get('/recycle/my')
            allData = recycleResponse.data || []
            break
          default:
            allData = []
        }
        
        // 应用关键词搜索
        if (queryParams.keyword) {
          const keyword = queryParams.keyword.toLowerCase()
          allData = allData.filter(item => {
            // 检查对象的所有字符串属性
            for (const key in item) {
              if (typeof item[key] === 'string' && item[key].toLowerCase().includes(keyword)) {
                return true
              }
            }
            return false
          })
        }
        
        // 应用筛选条件
        if (queryParams.filters) {
          for (const key in queryParams.filters) {
            const value = queryParams.filters[key]
            if (value) {
              allData = allData.filter(item => item[key] === value)
            }
          }
        }
        
        // 应用排序
        if (queryParams.sortBy) {
          allData.sort((a, b) => {
            const aValue = a[queryParams.sortBy]
            const bValue = b[queryParams.sortBy]
            
            if (typeof aValue === 'string' && typeof bValue === 'string') {
              return aValue.localeCompare(bValue)
            } else if (typeof aValue === 'number' && typeof bValue === 'number') {
              return aValue - bValue
            } else if (aValue instanceof Date && bValue instanceof Date) {
              return aValue - bValue
            } else if (aValue && bValue) {
              return String(aValue).localeCompare(String(bValue))
            } else {
              return 0
            }
          })
        }
        
        // 返回模拟的响应数据（无分页）
        return {
          data: allData
        }
      } catch (error) {
        console.error('本地快速查询失败:', error)
        throw error
      }
    }
  }

  /**
   * 保存查询条件
   * @param {string} name - 查询名称
   * @param {Object} queryParams - 查询参数
   */
  saveQuery(name, queryParams) {
    const savedQueries = JSON.parse(localStorage.getItem('savedQueries') || '[]')
    savedQueries.push({
      name,
      ...queryParams,
      module: this.baseUrl
    })
    localStorage.setItem('savedQueries', JSON.stringify(savedQueries))
  }

  /**
   * 获取已保存的查询条件
   * @returns {Array} 已保存的查询条件
   */
  getSavedQueries() {
    const savedQueries = JSON.parse(localStorage.getItem('savedQueries') || '[]')
    return savedQueries.filter(query => query.module === this.baseUrl)
  }

  /**
   * 删除已保存的查询条件
   * @param {string} name - 查询名称
   */
  deleteSavedQuery(name) {
    const savedQueries = JSON.parse(localStorage.getItem('savedQueries') || '[]')
    const filteredQueries = savedQueries.filter(query => !(query.module === this.baseUrl && query.name === name))
    localStorage.setItem('savedQueries', JSON.stringify(filteredQueries))
  }
}

// 创建各模块的查询服务实例
const clothingQueryService = new QueryService('/clothing')
const locationQueryService = new QueryService('/location')
const recycleQueryService = new QueryService('/recycle')

export {
  QueryService,
  clothingQueryService,
  locationQueryService,
  recycleQueryService
}