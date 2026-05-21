import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

// Example class (Replace this with the actual NetworkClient class you're testing)
class NetworkClient {
    fun fetchData(url: String): String {
        // Normally, this would make a network request
        return "Real Response"
    }
}

// Unit Test for NetworkClient
class NetworkClientTest {

    @Test
    fun `test fetchData returns expected response`() = runBlocking {
        // Arrange: Create a mock of NetworkClient
        val mockClient = mockk<NetworkClient>()

        // Mock behavior
        every { mockClient.fetchData("https://example.com") } returns "Mocked Response"

        // Act: Call the method
        val response = mockClient.fetchData("https://example.com")

        // Assert: Verify the response
        assertEquals("Mocked Response", response)

        // Verify that fetchData was called once
        verify(exactly = 1) { mockClient.fetchData("https://example.com") }
    }
}
