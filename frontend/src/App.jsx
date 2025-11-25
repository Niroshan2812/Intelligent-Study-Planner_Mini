import { useState } from 'react'
import axios from 'axios'
import './App.css'

function App() {
  const [studentId, setStudentId] = useState('')
  const [studentDetails, setStudentDetails] = useState({
    name: '',
    englishFluency: 3,
    tuitionHoursWeekly: 0,
    commuteFatigue: 3
  })
  const [schedule, setSchedule] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const handleInputChange = (e) => {
    const { name, value } = e.target
    setStudentDetails(prev => ({
      ...prev,
      [name]: value
    }))
  }

  const generateSchedule = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError(null)
    setSchedule([])

    try {
      // Step 1: Create/Update Student
      const studentResponse = await axios.post('http://localhost:8080/api/students', studentDetails)
      const newStudentId = studentResponse.data.id
      setStudentId(newStudentId)

      // Step 2: Generate Schedule
      const response = await axios.post(`http://localhost:8080/api/schedule/generate/${newStudentId}`)
      setSchedule(response.data)
    } catch (err) {
      setError('Failed to generate schedule. Please check your inputs.')
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="container">
      <header>
        <h1>Intelligent Study Planner</h1>
        <p>AI-powered scheduling for your academic success</p>
      </header>

      <main>
        <div className="card form-card">
          <h2>Generate Your Schedule</h2>
          <form onSubmit={generateSchedule}>
            <div className="form-group">
              <label htmlFor="name">Name</label>
              <input
                type="text"
                id="name"
                name="name"
                value={studentDetails.name}
                onChange={handleInputChange}
                placeholder="Enter your Name"
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="englishFluency">English Fluency (1-5)</label>
              <input
                type="number"
                id="englishFluency"
                name="englishFluency"
                min="1"
                max="5"
                value={studentDetails.englishFluency}
                onChange={handleInputChange}
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="tuitionHoursWeekly">Weekly Tuition Hours</label>
              <input
                type="number"
                id="tuitionHoursWeekly"
                name="tuitionHoursWeekly"
                min="0"
                value={studentDetails.tuitionHoursWeekly}
                onChange={handleInputChange}
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="commuteFatigue">Commute Fatigue (1-5)</label>
              <input
                type="number"
                id="commuteFatigue"
                name="commuteFatigue"
                min="1"
                max="5"
                value={studentDetails.commuteFatigue}
                onChange={handleInputChange}
                required
              />
            </div>

            <button type="submit" className="btn-primary" disabled={loading}>
              {loading ? 'Processing...' : 'Save & Generate Schedule'}
            </button>
          </form>
          {error && <div className="alert error">{error}</div>}
        </div>

        {schedule.length > 0 && (
          <div className="schedule-section">
            <h2>Your Study Schedule</h2>
            <div className="schedule-grid">
              {schedule.map((session) => (
                <div key={session.std_session_id || session.id} className="session-card">
                  <div className="session-header">
                    <h3>{session.subject.name}</h3>
                    <span className="badge">{session.subject.difficaltyLevel}</span>
                  </div>
                  <div className="session-body">
                    <p><strong>Topic:</strong> {session.title}</p>
                    <p><strong>Start:</strong> {new Date(session.startTime).toLocaleString('en-US', { weekday: 'short', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })}</p>
                    <p><strong>End:</strong> {new Date(session.endTime).toLocaleString('en-US', { hour: '2-digit', minute: '2-digit' })}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </main>
    </div>
  )
}

export default App
