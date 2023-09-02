package com.bizify.interfaces

import com.bizify.data.model.AodpList
import com.bizify.data.model.Customers
import com.bizify.data.model.Vehicles

/**
 * Created by Noushad N on 05-06-2022.
 */
interface VehicleClick {
    fun onItemClick(vehicles: Vehicles)
}