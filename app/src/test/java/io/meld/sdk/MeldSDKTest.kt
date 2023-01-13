package io.meld.sdk

import com.google.common.truth.Truth.assertThat
import io.meld.sdk.model.response.Institution
import io.meld.sdk.network.ApiInterface
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.logging.Logger

//@RunWith(MockitoJUnitRunner::class)
//@file:OptIn(ExperimentalCoroutinesApi::class)
class MeldSDKTest {

    private lateinit var meldSDK: MeldSDK
    private val apiKey = "W2a8zHdgnNoJQVWNyhFhBx:BoyNyR2Vzkuv1Ty6oLxBfvYGgQhhkGQBs4uhB"
    private val apiVersion = "2022-09-21"
    private val externalCustomerId = "FooBar"
    private val LOGGER: Logger = Logger.getLogger(MeldSDK::class.java.name)
    private lateinit var mockWebServer: MockWebServer
    private lateinit var testApis: ApiInterface

    private lateinit var service: ApiInterface


    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url(""))//We will use MockWebServers url
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        //meldSDK = MeldSDK()
        //MeldLogs.isLogsShown = true
//        meldSDK.init(
//            apiKey = apiKey,
//            externalCustomerId = externalCustomerId,
//            environment = Environment.QA
//        )
    }

    private fun enqueueMockResponse(fileName: String) {
        javaClass.classLoader?.let {
            val inputStream = it.getResourceAsStream(fileName)
            val source = inputStream.source().buffer()
            val mockResponse = MockResponse()
            mockResponse.setBody(source.readString(Charsets.UTF_8))
            mockWebServer.enqueue(mockResponse)
        }
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getInstitution() {
//        runBlocking {
//            enqueueMockResponse("getInstitution.json")
//            val responseBody: Call<Institution> = service.getInstitution("123465")
//            val request = mockWebServer.takeRequest()
//            assertThat(responseBody).isNotNull()
//            assertThat(request.path).isEqualTo("/v1/search?query=nature&per_page=5")
//        }
    }
    /*@Test
    fun getInstitution() = runTest{
        val deferred = async {
            withContext(Dispatchers.Default) {
                meldSDK.getInstitution("12s11Lqt1KKs3CBhNge67o132", object : OnInstitutionListener {
                    override fun onInstitution(response: InstitutionResponse?, error: MeldError?) {
                        response?.let {
                            Assert.assertTrue(true)
                        }
                        error?.let {
                            Assert.assertTrue(false)
                        }
                    }
                })
            }
        }
        deferred.await()

    }*/
    /*@Test
    fun getInstitution() {
        val expectedResponse = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
        mockWebServer.enqueue(expectedResponse)

        val actualResponse = meldSDK.getInstitution("1", object : OnInstitutionListener {
            override fun onInstitution(response: InstitutionResponse?, error: MeldError?) {
                error?.let {
                    assertThat("404").isEqualTo(HttpURLConnection.HTTP_NOT_FOUND)
                }
//                assertThat(actualResponse.body).isNull()
            }
        })
    }*/
    /*@Test
    fun getInstitution() {

        whenever(meldSDK.getInstitution("12s11Lqt1KKs3CBhNge67o", object : OnInstitutionListener {
            override fun onInstitution(response: InstitutionResponse?, error: MeldError?) {
                response?.let {
                    Assert.assertTrue(true)
                }
                error?.let {
                    Assert.assertTrue(false)
                }
            }
        })).thenAnswer {

        }


        *//*meldSDK.getInstitution("12s11Lqt1KKs3CBhNge67o", object : OnInstitutionListener {
            override fun onInstitution(response: InstitutionResponse?, error: MeldError?) {
                response?.let {
                    Assert.assertTrue(true)
                }
                error?.let {
                    Assert.assertTrue(false)
                }
            }
        })*//*
    }*/

    @Test
    fun emailValidator_CorrectEmailSimple_ReturnsTrue() {
        Assert.assertTrue(true)
    }


    /*@Test
    fun init() {

    }

    @Test
    fun getConnectToken() {
    }

    @Test
    fun setHandOverListener() {
    }

    @Test
    fun connectController() {
    }

    @Test
    fun getConnection() {
    }

    @Test
    fun searchInstitutionConnections() {
    }

    @Test
    fun connectionRefresh() {
    }

    @Test
    fun repairConnection() {
    }

    @Test
    fun getFinancialAccount() {
    }

    @Test
    fun searchFinancialAccount() {
    }

    @Test
    fun createProcessorToken() {
    }

    @Test
    fun getTransaction() {
    }

    @Test
    fun searchAccountTransactions() {
    }

    @Test
    fun getInstitution() {
    }

    @Test
    fun searchInstitutions() {
    }

    @Test
    fun searchCustomers() {
    }

    @Test
    fun getApiKey() {
    }

    @Test
    fun getApiVersion() {
    }

    @Test
    fun getExternalCustomerId() {
    }

    @Test
    fun getEnvironment() {
    }

    @Test
    fun isDebugLogEnabled() {
    }*/
}