package com.bizify.interfaces

import com.bizify.data.model.AodpList

/**
 * Created by Noushad N on 05-06-2022.
 */
interface ServiceItem {
    fun onItemAdd(position:Int, quantity:String,isQuantity:Boolean,unitPriceName : String)
    fun onItemRemove(position: Int)
}