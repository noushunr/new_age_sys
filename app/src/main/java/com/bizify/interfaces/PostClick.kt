package com.bizify.interfaces

import com.bizify.data.model.AodpList
import com.bizify.data.model.CreateJobResponse

/**
 * Created by Noushad N on 05-06-2022.
 */
interface PostClick {
    fun onItemClick(job: CreateJobResponse)
    fun onItemShare(job: CreateJobResponse)
}