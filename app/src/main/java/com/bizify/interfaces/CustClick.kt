package com.bizify.interfaces

import com.bizify.data.model.AodpList
import com.bizify.data.model.Customers

/**
 * Created by Noushad N on 05-06-2022.
 */
interface CustClick {
    fun onItemClick(customers: Customers)
}