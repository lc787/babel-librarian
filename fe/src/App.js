import logo from './logo.svg';
import './App.css';
import UserList from './components/user/UserList';
import UserRegister from './components/user/UserRegister';
function App() {
  return (
    <div className="App">
      <UserRegister/>
     <UserList/>
    </div>
  );
}

export default App;
