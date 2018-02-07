package com.blackcrowsys.api.models

import com.google.gson.annotations.SerializedName

data class AuthenticationRequest(val username: String,
                                 val password: String)

data class AuthenticationResponse(val username: String,
                                  @SerializedName("jwtToken")
                                  val jwtToken: String,
                                  @SerializedName("isAuthenticated")
                                  val isAuthenticated: Boolean,
                                  val permissions: Map<String, String>)