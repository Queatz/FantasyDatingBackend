package com.queatz.fantasydating.routes

import com.queatz.fantasydating.Arango
import com.queatz.fantasydating.DiscoveryPreferences
import com.queatz.fantasydating.Person
import com.queatz.fantasydating.PersonStory
import com.queatz.fantasydating.util.Time
import com.queatz.on.On
import io.ktor.application.ApplicationCall
import io.ktor.response.respond

class BootystrapRoute constructor(private val on: On) {
    suspend fun get(call: ApplicationCall) {
        val photos = listOf(
            "https://lh3.googleusercontent.com/80hJcieOULQhfT2hLS689_tNOCACzpilOYjTMvgw8aHH12Nk4hj7eTCsFdWY4lcC8laMoSAk8YIshlWMxRHELXYBE3UtDtWCK1_1uXFotpeUKn_D2AA0ZMcpQDwML8rBgDjMmFjaeW8",
            "https://lh3.googleusercontent.com/LE1DiS1zLJ-kdc639XxdC89NtNjBl3v8M7a2A_KX-8PaINqGvruYkcAnxOYu6Pbaa9asAdT75nHniyMUZRMrlSMoV0hH374dJlRdWzXhagh6ywZKvBZILyFEQnFLsnHLgIXQklUtcFo",
            "https://lh4.googleusercontent.com/luVnq4laYiTHUcQG3mEWirhNy6mJJ_aSTlYWcKHTfDFpQJOCKALKFUgJjJEQWeeadVtjv663soNxDSfm29Awgr2eYMDyDuHwMGUIOKho6zHMK-90FGR3Bs4ZMZYMSGmTk3Al58jcyKI",
            "https://lh6.googleusercontent.com/7HwiyGQgVsnaPSi0KDp_IE-UqKaDmZIlQQqKyJXAXHwiyVoYfkQfQwbCMGMf3nHhC3sIKzDIaSJq2-Aod9RErfPZlCVoLrHU4kAt3K7rwPXbKPW7Js4FmY4KXoIwTt4asEm6bBGvBkk",
            "https://lh3.googleusercontent.com/c6PnVBA7rKB_ofPEmcRXEQfNste2x5C3M5rmC1dlUUnoqHLlHg4R8wNZAS7LKFSBmL9f-5lB85oPwlBQ7Ib9aXTxHnHS9iBwdCkMQhTk4rgMsaKQyfObPg70xGB2_kH9GM8A8BOereo",
            "https://lh3.googleusercontent.com/F7BDjvSURaJuIEYUZ3ikrK6rdvqXjYmZvPrFoyyNpoHOAVOlzcheVO9WFCB3bZgkcvuhNxEQdKFiC-SGKOIDPdc8rPmHrTZFc6OovxKiM9VMcx93Bdf6kKMaZxFmVobxxZyXSh3kqfI",
            "https://lh5.googleusercontent.com/ecnggpWNeMRffzCDrEugrJqbolVoOXyd_jyHSRtuezPI461GIxdOXF6_e2yGE5Yf9BWf5QeSFwtaw8fsWdn8uKdyOtCZQDh7N86bNL5yXs7XsYHNJjpxUJoCsjdF2I1cTpBTwJuKLLs",
            "https://lh6.googleusercontent.com/0YmfPqHxMzYj0Q-8y4aP9bS2eCR0C_MCv_mGqAyqHB_i22MXfhRQ6wtRJw1AyzY31iQyBqG0bY6XvMyHa5mZlon6fvnPKi4yqQu5mGOU2k8SrN0E_pMbyfhAZF0OfCYRMMAZV5gNSwg",
            "https://lh3.googleusercontent.com/Nrcu5lmL_rDWNAi6lPRaEbWQkThT6l3gFzqhgxLJaUHNIhqvhtUZ1rQ20yXymtgsTY337Cx-yej-GTu-63DnoxmsbnJpfRYLtBZ5bXvDqfdUWxitEEzTuReiNlTGxKcwLdFCZHhll_0",
            "https://lh6.googleusercontent.com/Lzb4_BJHAXLd0uf4dVWcC_8VB7jQO7eo6j8cSs_lOSbDd0UHR7wJwmmAfXzomS7ykVLnVyr4Fb-83wADfEMHG_R9FDSRXN6WLCWC_rI3LNBbwGgpIdAj877Aw1F8XTjkb99tFK5RV-M",
            "https://lh4.googleusercontent.com/2y6LmnXWtS1OxJqKQ5364c6kiEexXm2O_IABU5gk7DDe_lDnihwjuRHv8kG0HyJMwciG0VLFXjvxyd-AB0lWzYlHV5lUQvbvsG-vz3uqmbdEAbHBaH7p0gOzLt2Wd1A2RgYA3IYkvdg",
            "https://lh3.googleusercontent.com/-gOVS09U5YirH7mB2vzPpdiSwZfyOb7CagjtiTRRbwVDWJQWKPOUrUmqhut9vm2mRCT_b6y9fxzwtLQe99N8cxrWcpwvJxJyfGrMFdnIG_It2ZwcdfqCHEwJq_qH_rQeAdAo69YWj58",
            "https://lh4.googleusercontent.com/iKTuuZ_JiTVDOEnqJq9FeiM7TffK_A_WOM60EJaHF2dvfSSxNMhtiVwjuwGbFk02KcnUIIlL2McCOCTkER7-LTHUIpHxUA8zwVoPJOzQWxu-g87qtIRuQDeWoCYEJxNhirvOUAI16Xg",
            "https://lh4.googleusercontent.com/lMPuPiWRVv0oQfnScRBBZY04CnuOZDWzMM6pDOKhC36n3TjWOL9PNUj9Ev4C25uGkbLquoMNIKWijIiYo_Lx_ht-zrIEFtU6Aidu4gCz8YH77IAXzf6le_rXlSLZyBvkmVTuU4opZkg",
            "https://lh5.googleusercontent.com/SFS7Oyua5gjVOatfPxpZmviFdlhY2-3TsdMKiK7Zz4p5zCtzCZYVnKH32f56tEGZXLpyvgpTWpGLRwd7ZB8XjloMFR5x0jBQeabynpwuW_xEfQLgdrol8pGPWApRXNITaGBZERG53Kw",
            "https://lh5.googleusercontent.com/Z7HKvSPvB-yj3xqvu1W8pOaHhzwS0uFVw7l6OoqDmMpBb4FZOZVkKCIFxB2T2mELkkQOXkfO4nAafk06-yGAO_zk22SLyTlgxW4RZAUjwphApCxu2i1CPbdXyG9ojVAa94yYkf3jwjA"
        )

        val ages = listOf(
            23,
            27,
            29,
            24,
            27
        )

        val names = listOf(
            "Emi",
            "Sal",
            "Liz",
            "Mary",
            "Samantha",
            "Jane"
        )

        val stories = listOf(
            "I love pretending I’m visiting Earth on an intergalactic mission.",
            "I love meeting new people in the rush of the city.",
            "I love taking photos of myself in the pool!",
            "I love going out for a run in the cool evening.",
            "I love meeting new people out in the countryside."
        )

        val fantasies = listOf(
            "I want a boy to masturbate and kiss me on a bench overlooking the lake."
        )

        fun personStory() = PersonStory(
            stories.random(),
            photos.random(),
            .5f,
            .75f
        )

        var girlsCreated = 0

        for (i in 0 until 99) {
            val girl = Person()
            girl.sex = "Girl"
            girl.age = ages.random()
            girl.active = true
            girl.name = names.random()
            girl.fantasy = fantasies.random()
            girl.approved = true
            girl.seen = on<Time>().now()
            girl.stories = listOf(personStory(), personStory(), personStory())

            val savedGirl = on<Arango>().save(girl)!!

            val ageMin = listOf(18, 20, 22, 24, 26, 28, 30, 35, 40).random()

            on<Arango>().save(DiscoveryPreferences(
                person = on<Arango>().ensureId(savedGirl.id!!),
                who = listOf("Boy", "Girl", "Person").random(),
                ageMin = ageMin,
                ageMax = ageMin + listOf(2, 5, 10).random()
            ))

            girlsCreated++
        }

        call.respond("$girlsCreated girls created")
    }
}
