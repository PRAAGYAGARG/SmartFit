// import './App.css'
import { Box, Button } from "@mui/material"
import { useContext, useEffect, useState } from "react"
import { AuthContext } from "react-oauth2-code-pkce"
import { useDispatch } from "react-redux";
import { BrowserRouter as Router, Navigate, Route, Routes, useLocation } from "react-router"
import { logout, setCredentials } from "./store/authSlice";
import ActivityForm from "./components/ActivityForm";
import ActivityList from "./components/ActivityList";
import ActivityDetail from "./components/ActivityDetail";


//To show ActivityForm and ActivityList on a single pg ie the homepg of app
//called in routing code in end of this file
const ActivitiesPage = () => {
  return (
    <Box sx={{ p: 2, border: '1px dashed grey' }}>

      <ActivityForm onActivityAdded = { () => window.location.reload()}/> 
        {/*When user click Add activity button then we want form to reload(.reload()) to empty 
        form so that user if want to add another activity then can easily just fill new details
        rather than first deleting old values*/}

      <ActivityList />
    </Box>
  );
}

function App() {
  
  //AuthContext comes from react-oauth2-code-pkce which gives us access to all these things like 
  //token , etc mentioned below
  const { token, tokenData, logIn, logOut, isAuthenticated } 
      = useContext(AuthContext);
  const dispatch = useDispatch();
  const [authReady, setAuthReady] = useState(false);

  //Used to update Redux store with token,user info ,user when Keycloak login succeeds
  useEffect(() => {
    if (token) {
      dispatch(setCredentials({token, user: tokenData}));
      setAuthReady(true);
    }
  }, [token, tokenData, dispatch]);

  return (
    //Routing logic - if no token show login button else show app
    <Router>
      {!token ? (
        <Button variant="contained"
          onClick={() => {logIn();}}>
          LOGIN
        </Button>
      ) : (
        <div>
         <Box component="section" sx={{ p: 2, border: '1px dashed grey' }}>
          <Button variant="contained" onClick={logout} >
            LOGOUT      
          </Button>
          <Routes>
            <Route path="/activities" element={<ActivitiesPage />}/>     {/*activity form + list*/}
            <Route path="/activities/:id" element={<ActivityDetail />}/> {/*activity details */}
            <Route path="/" element={token ? <Navigate to="/activities" replace/> :
                                  <div>Welcome! Please login</div>}/>
          </Routes>
        </Box>
        </div>
      )}
    </Router>
  )
}

export default App
